package com.ecommerce.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public record CartItemReq(
        @NotNull @Positive Long productId
        , @NotNull @Positive Integer quantity
) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItemReq that = (CartItemReq) o;
        return Objects.equals(productId(), that.productId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId());
    }
}
