package com.ecommerce.orders.dtos;

import com.ecommerce.Payment.entity.PaymentMethod;
import com.ecommerce.orders.entity.OrderState;
import com.ecommerce.util.Money;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTOView {
    private Long order_id;
    private OrderState orderState;
    private PaymentMethod paymentMethod;
    private ShippingDTO shippingDTO;
    private Money total;
    private List<OrderItemDTO> orderItemDTOS;
}
