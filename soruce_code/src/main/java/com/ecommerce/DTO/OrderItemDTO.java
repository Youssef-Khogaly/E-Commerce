package com.ecommerce.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Setter
@Getter
public class OrderItemDTO {
    private String name;
    private String description;
    private int quantity;
    private long subtotalInCents;
}
