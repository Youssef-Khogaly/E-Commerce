package com.ecommerce.Cart.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CartRequest(@NotNull @Positive Long product_id , @NotNull @PositiveOrZero Integer quantity) {

}
