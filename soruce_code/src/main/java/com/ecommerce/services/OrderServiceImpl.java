package com.ecommerce.services;

import com.ecommerce.DTO.*;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Mappers.OrderDtoViewMapper;
import com.ecommerce.Mappers.OrderMapper;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderJpaRepo;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import com.ecommerce.services.interfaces.IPaymentService;
import com.ecommerce.services.interfaces.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderJpaRepo orderJpaRepo;
    private final IPaymentGatewayService paymentGatewayService;
    private final OrderMapper orderMapper;
    private final OrderDtoViewMapper orderDtoViewMapper;
    private final IPaymentService paymentService;
    @Override
    public Order createOrder(OrderDTO orderDTO) {
        if(orderDTO.getOrderState() == null || orderDTO.getOrderState() != OrderState.PENDING)
            throw new BadRequestException("Can't create new order with null or not pending state");

        Order order = orderMapper.from(orderDTO);

        orderDTO.setOrder_id(order.getId());
        orderDTO.getPaymentDTO().setId(order.getId());
        return order;
    }

    public Order createOrder(Order order){
        if(order == null)
            throw new IllegalArgumentException("Can't presist null order");

        if(order.getRecipientName() == null || order.getRecipientPhone() == null || order.getBuilding() == null || order.getStreet() == null || order.getCity() == null
        || order.getRecipientName().isBlank() || order.getRecipientPhone().isBlank() || order.getBuilding().isBlank()|| order.getStreet().isBlank() || order.getCity().isBlank())
            throw new BadRequestException("Order creation failed incompleted shipping address");

        if(order.getOrderItems() == null || order.getOrderItems().isEmpty())
            throw new BadRequestException("Order creation failed can't create order without items");


        return orderJpaRepo.save(order);
    }

    @Override
    public CompletableFuture<Order> createOrderAsync(Order order) {
        return CompletableFuture.supplyAsync(() -> this.createOrder(order));
    }

    @Override
    public List<OrderDTOView> getOrders(Long cust_id) {

        List<Order> orders = orderJpaRepo.findAllByCustomerIdListView(cust_id);
        return orders.stream().map(orderDtoViewMapper::from).toList();
    }
    public OrderDTOView getOrder(Long customer_id , UUID orderId){

        Order order = orderJpaRepo.findByCustomerIdAndOrderId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");

        return orderDtoViewMapper.from(order);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelOrder(Long customer_id, UUID orderId) {
        Order order = orderJpaRepo.findWithAllByIdAndCustId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");
        if(order.getOrderState() ==  OrderState.PENDING){
            // cancel session
            Payment payment = paymentService.findByOrderId(orderId).orElse(null);
            if(payment == null){
                log.error("unexpected !! Order without Payment , orderId: {}" , orderId);
                return;
            }
            cancelSession(payment.getSession_id() , payment.getPaymentMethod());
            // don't handle it stock un reservation logic here or update states
            // gateway gonna send webhook and event will be handled async

        }
        else if (order.getOrderState() == OrderState.SHIPPING || order.getOrderState() == OrderState.DELIVERED)
            throw new BadRequestException("can't cancel the order already paid");
        else if (OrderState.REFUNDED == order.getOrderState())
            throw new BadRequestException("can't cancel refunded order");
        else {
            // already canceled or expired
            // nothing
        }

    }

    @Override
    public void deleteOrder(UUID orderId) {
        orderJpaRepo.deleteById(orderId);
    }


    private void cancelSession(String sessionId , PaymentMethod method){
        paymentGatewayService.cancelSession(sessionId,method);
    }

}
