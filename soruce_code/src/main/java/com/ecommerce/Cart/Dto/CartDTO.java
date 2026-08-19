package com.ecommerce.Cart.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {

    private List<CartItemDTO> items;
    private long cartId;
}
