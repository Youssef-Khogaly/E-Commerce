package com.ecommerce.DTO.Requests;

import com.ecommerce.DTO.CartItemReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

public record CartRequest(@NotNull @Positive Long product_id , @NotNull @PositiveOrZero Integer quantity) {

}
