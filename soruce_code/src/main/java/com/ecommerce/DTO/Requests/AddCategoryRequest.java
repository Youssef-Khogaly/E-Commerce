package com.ecommerce.DTO.Requests;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;

import java.util.Objects;

public record AddCategoryRequest(@NotEmpty String name , @Nullable Long parentId) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AddCategoryRequest that = (AddCategoryRequest) o;
        return Objects.equals(name(), that.name());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name());
    }
}
