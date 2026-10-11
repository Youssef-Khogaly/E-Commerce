package com.ecommerce.Images.dtos;

import com.ecommerce.Images.entity.ProductImages;
import org.springframework.stereotype.Component;

@Component
public class ProductImageDtoMapper {


    public ProductImageDto from(ProductImages productImage)
    {
        return ProductImageDto.builder().id(productImage.getImage().getId())
                .url(productImage.getImage().getImageUrl())
                .isMain(productImage.isMain()).build();
    }
}
