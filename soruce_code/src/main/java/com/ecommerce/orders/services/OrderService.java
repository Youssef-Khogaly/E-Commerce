package com.ecommerce.orders.services;

import com.ecommerce.orders.dtos.OrderDTO;
import com.ecommerce.orders.dtos.OrderDTOView;
import com.ecommerce.orders.entity.Order;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OrderService {


     Order createOrder(OrderDTO orderDTO);
     public Order createOrder(Order order);
     public CompletableFuture<Order> createOrderAsync(Order order);
     List<OrderDTOView> getOrders(Long cust_id);
     OrderDTOView getOrder(Long customer_id , Long orderId);
     void cancelOrder(Long customer_id , Long orderId);
     void deleteOrder(Long orderId);

}
