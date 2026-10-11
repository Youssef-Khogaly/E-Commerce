package com.ecommerce.Product.services.query;

import com.ecommerce.Product.dtos.ProductDTO;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public interface ProductDtoQueryService extends ProductQueryService<ProductDTO>{

    enum ProductDtoFields{
        CATEGORY,IMAGES
    }
    ProductDTO findById(Long productId, EnumSet<ProductDtoFields> includes);
    Map<Long,ProductDTO> findAllByIds(Set<Long> ids,EnumSet<ProductDtoFields> includes);
}
