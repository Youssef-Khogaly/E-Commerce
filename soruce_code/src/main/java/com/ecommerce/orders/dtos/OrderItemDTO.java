package com.ecommerce.orders.dtos;

import com.ecommerce.util.Money;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemDTO {
    private Long product_id;
    private String name;
    private String description;
    private int quantity;
    private Money unitPrice;
    private Money finalDiscount;
    private Money subtotal;
}
