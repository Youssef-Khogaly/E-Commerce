package com.ecommerce.Product.services;

import com.ecommerce.Product.dtos.ProductDTO;

import java.util.Set;

public interface ProductDtoFacade {


    ProductDTO findById(Long productId);
    Set<ProductDTO> findAllByIds(Set<Long> ids);

}
