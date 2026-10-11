package com.ecommerce.Product.services.query;

import com.ecommerce.Product.dtos.ProductRawDto;
import com.ecommerce.Product.services.crud.ProductCrudService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductRawQueryImpl implements ProductQueryService<ProductRawDto>{

    private final ProductCrudService productCrudService;

    public ProductRawQueryImpl(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @Override
    public ProductRawDto findById(Long productId) {
        return ProductRawDto.from( productCrudService.getProductById(productId));
    }

    @Override
    public Map<Long, ProductRawDto> findAllByIds(Set<Long> ids) {
        return productCrudService
                .getProductsByIds(ids).values().stream().map(ProductRawDto::from)
                .collect(Collectors.toMap(ProductRawDto::id, p ->p ));
    }
}
