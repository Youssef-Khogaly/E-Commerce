package com.ecommerce.services.checkout;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.Requests.CheckoutReq;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.Inegration.PaymentGateWay.Model.*;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.interfaces.CartService;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import com.ecommerce.services.interfaces.OrderService;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
    public CheckoutService(StockService stockService, IPaymentGatewayService paymentGatewayService, CartService cartService, OrderService orderService) {
        this.stockService = stockService;
        this.paymentGatewayService = paymentGatewayService;
        this.cartService = cartService;
        this.orderService = orderService;
    }

    private List<PaymentGatewayLineItem> toItemModel(List<OrderItem> orderItems){

        return orderItems.stream().map(p ->{
           var item = new PaymentGatewayLineItem();
           item.setItemName(p.getName());
           item.setCurrency(Currency.getInstance("EGP"));
           item.setItemDescription(p.getDescription());
           item.setQuantity(p.getQuantity());
           item.setFinalAmountInCents(p.getSubTotalInCents());
           item.setImgUrl(null);
           return item;
        }).toList();

    }
    private PaymentGatewayOrderModel createPaymentOrderModel(long customer_id , List<OrderItem>orderItems){
        PaymentGatewayOrderModel paymentOrder = new PaymentGatewayOrderModel();
        paymentOrder.setCustomer_id(customer_id);
        paymentOrder.setItems(toItemModel(orderItems));
        return  paymentOrder;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public PaymentSession checkout(CheckoutReq req , Long cust_id) {

        CartDTO cartDTO = cartService.getCart(cust_id);
        if(cartDTO.getItems().isEmpty())
            throw new ConflictException("can't checkout with empty cart");

        final Map<Long , Integer>id_quantityMap = cartDTO.getItems().stream().collect(Collectors.toUnmodifiableMap(i ->i.getProductDTO().getId() , CartItemDTO::getQuantity));
        try{
            stockService.updatestock(id_quantityMap, StockService.StockOperation.RESERVE);
        }catch (OptimisticLockException e){
            throw new ConflictException("couldn't lock the stock try again later");
        }

        try{
            // create order
            Order order = orderService.createOrder(cartDTO,req.shipping(),req.paymentMethod());
            // create model
            PaymentGatewayOrderModel paymentOrder = createPaymentOrderModel(cust_id,order.getOrderItems());
            // set order id and payment id to the model
            paymentOrder.setOrder_id(order.getId());
            paymentOrder.setPayment_id(order.getPayment().getId());

            SessionGenerationCommand command =  new SessionGenerationCommand(paymentOrder, Duration.ofMinutes(40),"https://www.google.com/","https://www.google.com/" , req.paymentMethod());


            // add the session id and expiration to the payment order database
            PaymentSession paymentSession = paymentGatewayService.generateSessionUrl(command);
            order.getPayment().setSession_id(paymentSession.getSession_id());
            order.getPayment().setExpireAt(paymentSession.getExpireAt().getEpochSecond());
            return paymentSession;
        }
        catch (Throwable e) {
            stockService.updatestock(id_quantityMap, StockService.StockOperation.RELEASE);
            throw e;
        }

    }



}
