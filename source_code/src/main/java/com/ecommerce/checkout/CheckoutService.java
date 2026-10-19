package com.ecommerce.checkout;

import com.ecommerce.Cart.Dto.CartDTO;
import com.ecommerce.Cart.Dto.CartItemDTO;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Images.dtos.ProductImageDto;
import com.ecommerce.Inegration.PaymentGateWay.Model.*;
import com.ecommerce.checkout.async.*;
import com.ecommerce.inventory.exceptions.InventoryAvailablePoolInsufficientException;
import com.ecommerce.inventory.exceptions.InventoryInsufficientStock;
import com.ecommerce.inventory.service.InventoryReservationService;
import com.ecommerce.inventory.service.StockPoolRefillService;
import com.ecommerce.orders.mappers.OrderMapper;
import com.ecommerce.orders.entity.Order;
import com.ecommerce.orders.entity.OrderItem;
import com.ecommerce.orders.entity.OrderState;
import com.ecommerce.Cart.services.CartService;
import com.ecommerce.orders.services.OrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CheckoutService {

    private final InventoryReservationService inventoryReservationService;
    private final CartService cartService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final TransactionTemplate orderCheckOutTransactionTemplate;
    private final Duration checkoutSessionTimeOut = Duration.ofMinutes(30);
    private final CheckoutJobRepo checkoutJobRepo;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StockPoolRefillService stockPoolRefillService;
    public CheckoutService(InventoryReservationService inventoryReservationService, CartService cartService, OrderService orderService, OrderMapper orderMapper, TransactionTemplate transactionTemplate, CheckoutSessionGenerationPublisher sessionGenerationPublisher, CheckoutJobRepo checkoutJobRepo, ApplicationEventPublisher applicationEventPublisher, StockPoolRefillService stockPoolRefillService) {
        this.inventoryReservationService = inventoryReservationService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.orderCheckOutTransactionTemplate = transactionTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
        this.stockPoolRefillService = stockPoolRefillService;
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.checkoutJobRepo = checkoutJobRepo;
    }

    private List<PaymentGatewayLineItem> toItemModel(List<OrderItem> orderItems, Map<Long , List<String>>productId_Images){

        return orderItems.stream().map(p ->{
           var item = new PaymentGatewayLineItem();
           item.setItemName(p.getName());
           item.setCurrency(Currency.getInstance(p.getCurrencyCode()));
           item.setItemDescription(p.getDescription());
           item.setQuantity(p.getQuantity());
           item.setFinalAmountInCents(p.getSubTotalInCents());
          List<String> images  = productId_Images.get(p.getProduct_id());
           if(images != null && !images.isEmpty())
                item.setImgesUrl(images);
           return item;
        }).toList();

    }
    private PaymentGatewayOrderModel createPaymentOrderModel(Order order,Map<Long , List<String>>productId_Images){
        PaymentGatewayOrderModel paymentOrder = new PaymentGatewayOrderModel();
        paymentOrder.setCustomer_id(order.getCustomer().getId());
        paymentOrder.setItems(toItemModel(order.getOrderItems(),productId_Images));
        paymentOrder.setOrder_id(order.getId());
        return  paymentOrder;
    }


    public CheckoutJob checkout(CheckoutReq req , Long cust_id)  {

        CartDTO cart = cartService.getCart(cust_id);
        if(cart.getItems().isEmpty())
                throw new BadRequestException("Can't checkout empty cart");

        final Map<Long , List<String>>productId_Images = cart.getItems().stream()
                .collect(Collectors.toMap(i -> i.getProductDTO().getId(), i -> {
                    if(i.getProductDTO().getImagesUrl() == null)
                        return List.of();
                    return  i.getProductDTO().getImagesUrl().stream().map(ProductImageDto::url).toList();
                } ) );

        final Map<Long,Integer>id_quantityMap =  cart.getItems().stream().collect(Collectors.toUnmodifiableMap(i -> i.getProductDTO().getId() , CartItemDTO::getQuantity));

            Order order = orderMapper.from(cart,req.shipping());
            order.setState(OrderState.PENDING);
            order.setPaymentMethod(req.paymentMethod());
            order.setExpireAt(checkoutSessionTimeOut.toSeconds());

        PaymentGatewayOrderModel paymentOrder = createPaymentOrderModel(order,productId_Images);
        var before = Instant.now();
        try{

            return checkoutTransaction(id_quantityMap,order,paymentOrder);
        } catch (InventoryAvailablePoolInsufficientException e) {
            long beforeRefill = Instant.now().toEpochMilli();
            List<Long> insufficientStockIds = new ArrayList<>();
            for(var id : e.getId_required_map().keySet()){
                try{
                    stockPoolRefillService.refill(id);
                } catch (InventoryInsufficientStock ex) {
                    insufficientStockIds.addAll(ex.getProductIds());
                }
            }
            long time = Instant.now().toEpochMilli() - beforeRefill;
            log.debug("refill transaction time: {}" , time);
            if(!insufficientStockIds.isEmpty())
                throw new InventoryInsufficientStock("Insufficient stock" ,insufficientStockIds );

//             refill successfully , try again

            try{
                return checkoutTransaction(id_quantityMap,order,paymentOrder);
            }catch (InventoryAvailablePoolInsufficientException ex){

                throw new InventoryInsufficientStock("Insufficient stock", e.getId_required_map().keySet() );
            }
        }

        finally {
            long time = Instant.now().toEpochMilli() - before.toEpochMilli();
            final String thread = Thread.currentThread().getName();
            log.debug("TESTTransaction {} running time:{}" , thread, time);
            if(time > 20){
                log.debug("TESTTransaction {} , run more than 20ms" , thread);
            }
        }


    }
    private CheckoutJob checkoutTransaction(Map<Long,Integer> id_quantityMap,Order order,PaymentGatewayOrderModel paymentOrder){
        return orderCheckOutTransactionTemplate.execute((status) ->{
            long t1 = Instant.now().toEpochMilli();;
            long resId = inventoryReservationService.try_reserve(id_quantityMap);
            log.debug("reserve stock time, {}" , Instant.now().toEpochMilli() - t1);
            order.setReservationId(resId);
            var order2 = orderService.createOrder(order);
            paymentOrder.setOrder_id(order2.getId());

            SessionGenerationCommand command =  new SessionGenerationCommand(paymentOrder, checkoutSessionTimeOut,"https://www.google.com/","https://www.google.com/" ,order.getPaymentMethod(),Map.of("resID",String.valueOf(resId)));
            var job = createJob(command);
             applicationEventPublisher.publishEvent(new CheckoutJobCreatedEvent(job));
            return job;
        });
    }
    private CheckoutJob createJob(SessionGenerationCommand command)
    {
        var checkoutJob = new CheckoutJob();
        checkoutJob.setOrderId(command.getOrderModel().getOrder_id());
        checkoutJob.setCommand(command);
        checkoutJob.setStatus(CheckoutJobStatus.PROCESSING);
        checkoutJob.setCreatedAt(Instant.now());
        checkoutJob.setUserId(command.getOrderModel().getCustomer_id());
        return checkoutJobRepo.save(checkoutJob);
    }
    public CheckoutJob findCheckoutJob(UUID jobId, Long custId)
    {
        return checkoutJobRepo.findByIdAndUserId(jobId,custId).orElseThrow(() -> new NotFoundException("Checkout job does not exists, id:" + jobId.toString()));
    }



}
