package com.ecommerce.services;

import com.ecommerce.DTO.*;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Mappers.OrderDtoViewMapper;
import com.ecommerce.Mappers.OrderMapper;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderJpaRepo;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import com.ecommerce.services.interfaces.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderJpaRepo orderJpaRepo;
    private final IPaymentGatewayService paymentGatewayService;
    private final OrderMapper orderMapper;
    private final OrderDtoViewMapper orderDtoViewMapper;
    private final StockService stockService;
    @Override
    public Order createOrder(OrderDTO orderDTO) {
        if(orderDTO.getOrderState() == null || orderDTO.getOrderState() != OrderState.PENDING)
            throw new BadRequestException("Can't create new order with null or not pending state");

        Order order = orderMapper.from(orderDTO);

        orderDTO.setOrder_id(order.getId());
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
    public OrderDTOView getOrder(Long customer_id , Long orderId){

        Order order = orderJpaRepo.findByCustomerIdAndOrderId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");

        return orderDtoViewMapper.from(order);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelOrder(Long customer_id, Long orderId) {
        Order order = orderJpaRepo.findWithAllByIdAndCustId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");


        switch (order.getState())
        {
            case CANCELED,EXPIRED -> {
                // ignore already cancelled
                return ;
            }
            case REFUNDED -> throw new BadRequestException("can't cancel,  order is already refunded");
            case SHIPPING -> throw new BadRequestException("can't cancel,  order is already shipped");
            case DELIVERED -> throw new BadRequestException("can't cancel,  order is already delivered");
            case PENDING -> {
                order.setState(OrderState.CANCELED);
                var idQuantityMap = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id, OrderItem::getQuantity));
                stockService.release(idQuantityMap);
            }
            case PROCESSING -> {
                order.setState(OrderState.CANCELED);
                var idQuantityMap = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id, OrderItem::getQuantity));
                stockService.add(idQuantityMap);
                // issue refund!! to do later
            }
        }

    }

    @Override
    public void deleteOrder(Long orderId) {
        orderJpaRepo.deleteById(orderId);
    }


    private void cancelSession(String sessionId , PaymentMethod method){
        paymentGatewayService.cancelSession(sessionId,method);
    }

}
