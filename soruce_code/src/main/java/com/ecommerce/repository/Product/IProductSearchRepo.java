package com.ecommerce.repository.Product;

import com.ecommerce.entities.Products.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductSearchRepo {

    public Page<Product> searchForProducts(@Nullable String name , @Nullable Integer categoryId , @PositiveOrZero long minPrice
            , @Positive long maxPrice, @NotNull Pageable pageable);
}
