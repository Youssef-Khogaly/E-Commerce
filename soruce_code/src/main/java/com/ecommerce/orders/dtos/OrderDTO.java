package com.ecommerce.orders.dtos;

import com.ecommerce.Payment.entity.PaymentMethod;
import com.ecommerce.orders.entity.OrderState;
import com.ecommerce.util.Money;
import lombok.*;

import java.util.List;


@Getter
@Setter
public class OrderDTO {
    private  Long order_id;
    private  Long cust_id;
    private OrderState orderState;
    private  PaymentMethod paymentMethod;
    private  ShippingDTO shippingDTO;
    private Money total;
    private  List<OrderItemDTO> orderItemDTOS;
}
