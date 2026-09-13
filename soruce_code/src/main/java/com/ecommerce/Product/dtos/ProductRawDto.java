package com.ecommerce.Product.dtos;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.util.Money;
import lombok.Builder;

@Builder
public record ProductRawDto(long id, String name, String description, Money price,DiscountDTO discount) {


   public static ProductRawDto from(Product product){
       return ProductRawDto.builder().id(product.getId())
                .name(product.getTitle())
                .description(product.getDescription())
                .price(product.getPriceMoney())
                .discount(new DiscountDTO()).build();
    }
}
