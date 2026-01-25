package com.ecommerce.services.interfaces;

import com.ecommerce.DTO.OrderDTO;
import com.ecommerce.DTO.OrderDTOView;
import com.ecommerce.entities.orders.Order;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderService {


     Order createOrder(OrderDTO orderDTO);
     public Order createOrder(Order order);
     public CompletableFuture<Order> createOrderAsync(Order order);
     List<OrderDTOView> getOrders(Long cust_id);
     OrderDTOView getOrder(Long customer_id , UUID orderId);
     void cancelOrder(Long customer_id , UUID orderId);
}
