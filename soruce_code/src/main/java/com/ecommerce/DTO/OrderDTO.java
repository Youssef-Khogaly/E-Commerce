package com.ecommerce.DTO;

import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.orders.OrderState;
import lombok.*;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class OrderDTO {
    private  UUID order_id;
    private  Long cust_id;
    private  OrderState orderState;
    private  PaymentMethod paymentMethod;
    private  ShippingDTO shippingDTO;
    private  Money total;
    private  PaymentDTO paymentDTO;
    private  List<OrderItemDTO> orderItemDTOS;
}
