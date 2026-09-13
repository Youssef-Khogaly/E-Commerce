package com.ecommerce.Product.dtos;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.util.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.Objects;


@Builder
public record ProductRawDto(long id, String name, String description, Money price,
                            DiscountDTO discount) implements Serializable {

    public static ProductRawDto from(Product product) {
        return ProductRawDto.builder()
                .id(product.getId())
                .name(product.getTitle())
                .description(product.getDescription())
                .price(product.getPriceMoney())
                .discount(new DiscountDTO())
                .build();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProductRawDto that)) return false;
        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }
}
