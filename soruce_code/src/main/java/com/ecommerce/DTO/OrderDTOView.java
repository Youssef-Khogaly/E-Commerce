package com.ecommerce.DTO;

import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.orders.OrderState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTOView {
    private UUID order_id;
    private OrderState orderState;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private ShippingDTO shippingDTO;
    private long totalInCents;
    private String currency;
    private List<OrderItemDTO> orderItemDTOS;
}
