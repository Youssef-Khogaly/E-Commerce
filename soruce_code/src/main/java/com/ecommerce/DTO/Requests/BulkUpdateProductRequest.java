package com.ecommerce.DTO.Requests;

import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.Objects;

public record BulkUpdateProductRequest(
        @NotNull BigInteger id, @NotNull AddProductRequest fieldsToUpdate

) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BulkUpdateProductRequest that = (BulkUpdateProductRequest) o;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}