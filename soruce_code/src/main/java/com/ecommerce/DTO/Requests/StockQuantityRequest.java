package com.ecommerce.DTO.Requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

public record StockQuantityRequest(@Positive @NotNull Long id , @Positive @NotNull Integer quantity) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StockQuantityRequest that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
