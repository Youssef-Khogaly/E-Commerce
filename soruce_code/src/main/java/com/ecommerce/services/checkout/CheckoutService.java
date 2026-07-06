package com.ecommerce.services.checkout;

import com.ecommerce.DTO.Requests.CheckoutReq;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Inegration.PaymentGateWay.Model.*;
import com.ecommerce.Mappers.OrderMapper;
import com.ecommerce.entities.Carts.Cart;
import com.ecommerce.entities.Carts.CartItem;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderJpaRepo;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.interfaces.CartService;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import com.ecommerce.services.interfaces.OrderService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
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

    public CheckoutService(StockService stockService, IPaymentGatewayService paymentGatewayService, CartService cartService, OrderService orderService, OrderMapper orderMapper, OrderJpaRepo orderJpaRepo, TransactionTemplate transactionTemplate) {
        this.stockService = stockService;
        this.paymentGatewayService = paymentGatewayService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.orderJpaRepo = orderJpaRepo;
        this.orderCheckOutTransactionTemplate = transactionTemplate;
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
        paymentOrder.setCustomer_id(order.getCustomer_id());
        paymentOrder.setItems(toItemModel(order.getOrderItems(),productId_Images));
        paymentOrder.setOrder_id(order.getId());
        return  paymentOrder;
    }



    public PaymentSession checkout(CheckoutReq req , Long cust_id)  {

        Cart cart = cartService.getCartForCheckout(cust_id).orElseThrow(() -> new BadRequestException("Customer id doesn't exists"));
        if(cart.getCartItemSet().isEmpty())
                throw new BadRequestException("Can't checkout empty cart");
        final Map<Long , List<String>>productId_Images = cart.getCartItemSet().stream().collect(Collectors.toMap(i -> i.getProduct().getId()
                , i -> i.getProduct().getImagesList().stream().map(pi -> pi.getImage().getImageUrl()).toList()));

        final Map<Long,Integer>id_quantityMap =  cart.getCartItemSet().stream().collect(Collectors.toUnmodifiableMap(i -> i.getProduct().getId() , CartItem::getQuantity));

            Order order = orderMapper.from(cart,req.shipping());
            PaymentSession paymentSession = null;
            order.setState(OrderState.PENDING);
            order.setPaymentMethod(req.paymentMethod());


            final Order finalOrder = order;
            order =  orderCheckOutTransactionTemplate.execute((status) ->{
                stockService.updatestock(id_quantityMap, StockService.StockOperation.RESERVE);
                return  orderService.createOrder(finalOrder);
            });




            try{
                // create model
                PaymentGatewayOrderModel paymentOrder = createPaymentOrderModel(order,productId_Images);

                SessionGenerationCommand command =  new SessionGenerationCommand(paymentOrder, Duration.ofMinutes(40),"https://www.google.com/","https://www.google.com/" ,order.getPaymentMethod());

                paymentSession = paymentGatewayService.generateSessionUrl(command);
                order.setSession_id(paymentSession.getSession_id());
                order.setExpireAt(paymentSession.getExpireAt().toEpochMilli());
                orderJpaRepo.save(order);
            }catch (Exception e)
            {
                // session creation failed!!
                stockService.updatestock(id_quantityMap, StockService.StockOperation.RELEASE);
                orderService.deleteOrder(order.getId());
                throw new RuntimeException(e.getCause());
            }


            return paymentSession;


    }



}
