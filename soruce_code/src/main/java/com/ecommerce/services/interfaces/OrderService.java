package com.ecommerce.services.interfaces;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.OrderDTOView;
import com.ecommerce.DTO.ShippingDTO;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.orders.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {


     Order createOrder(CartDTO cartDTO, ShippingDTO shippingDTO, PaymentMethod method);

     List<OrderDTOView> getOrders(Long cust_id);
     OrderDTOView getOrder(Long customer_id , UUID orderId);
     void cancelOrder(Long customer_id , UUID orderId);
}
