package com.ecommerce.checkout;

import com.ecommerce.Cart.Dto.CartDTO;
import com.ecommerce.Cart.Dto.CartItemDTO;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Inegration.PaymentGateWay.Model.*;
import com.ecommerce.orders.mappers.OrderMapper;
import com.ecommerce.orders.entity.Order;
import com.ecommerce.orders.entity.OrderItem;
import com.ecommerce.orders.entity.OrderState;
import com.ecommerce.orders.repos.OrderJpaRepo;
import com.ecommerce.User.UsersRepo.CustomerJpaRepo;
import com.ecommerce.Stock.service.StockService;
import com.ecommerce.Cart.services.CartService;
import com.ecommerce.Payment.services.IPaymentGatewayService;
import com.ecommerce.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CheckoutService {

    private final StockService stockService;
    private final IPaymentGatewayService paymentGatewayService;
    private final CartService cartService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderJpaRepo orderJpaRepo;
    private final TransactionTemplate orderCheckOutTransactionTemplate;
    private final Duration checkoutSessionTimeOut = Duration.ofMinutes(40);
    private final CustomerJpaRepo customerJpaRepo;
    public CheckoutService(StockService stockService, IPaymentGatewayService paymentGatewayService, CartService cartService, OrderService orderService, OrderMapper orderMapper, OrderJpaRepo orderJpaRepo, TransactionTemplate transactionTemplate, CustomerJpaRepo customerJpaRepo) {
        this.stockService = stockService;
        this.paymentGatewayService = paymentGatewayService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.orderJpaRepo = orderJpaRepo;
        this.orderCheckOutTransactionTemplate = transactionTemplate;
        this.customerJpaRepo = customerJpaRepo;
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
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



    public PaymentSession checkout(CheckoutReq req , Long cust_id)  {

        CartDTO cart = cartService.getCart(cust_id);
        if(cart.getItems().isEmpty())
                throw new BadRequestException("Can't checkout empty cart");

        final Map<Long , List<String>>productId_Images = cart.getItems().stream()
                .collect(Collectors.toMap(i -> i.getProductDTO().getId(), i -> {
                    if(i.getProductDTO().getMainImgUrl() == null)
                        return List.of();
                    return  List.of(i.getProductDTO().getMainImgUrl());
                } ) );

        final Map<Long,Integer>id_quantityMap =  cart.getItems().stream().collect(Collectors.toUnmodifiableMap(i -> i.getProductDTO().getId() , CartItemDTO::getQuantity));

            Order order = orderMapper.from(cart,req.shipping());
            PaymentSession paymentSession = null;
            order.setState(OrderState.PENDING);
            order.setPaymentMethod(req.paymentMethod());
            order.setExpireAt(checkoutSessionTimeOut.toSeconds());
            final Order finalOrder = order;
            order =  orderCheckOutTransactionTemplate.execute((status) ->{
                stockService.reserve(id_quantityMap);
                return  orderService.createOrder(finalOrder);
            });




            try{
                // create model
                PaymentGatewayOrderModel paymentOrder = createPaymentOrderModel(order,productId_Images);

                SessionGenerationCommand command =  new SessionGenerationCommand(paymentOrder, Duration.ofMinutes(40),"https://www.google.com/","https://www.google.com/" ,order.getPaymentMethod());

                paymentSession = paymentGatewayService.generateSessionUrl(command);
                order.setSession_id(paymentSession.getSession_id());
                orderJpaRepo.save(order);
            }catch (Exception e)
            {
                // session creation failed!!

                final Order finalOrderDelete = order;
                orderCheckOutTransactionTemplate.executeWithoutResult((status) ->{
                    stockService.release(id_quantityMap);
                    orderService.deleteOrder(finalOrderDelete.getId());
                });
                throw new RuntimeException(e.getCause());
            }


            return paymentSession;


    }



}
