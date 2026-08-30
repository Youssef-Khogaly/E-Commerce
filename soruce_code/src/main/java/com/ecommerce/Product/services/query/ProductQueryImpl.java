package com.ecommerce.Product.services.query;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.services.crud.ProductCrudService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class ProductQueryImpl implements ProductQueryService<Product>{

    private final ProductCrudService productCrudService;

    public ProductQueryImpl(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @Override
    public Product findById(Long productId) {
        return productCrudService.getProductById(productId);
    }

    @Override
    public Map<Long, Product> findAllByIds(Set<Long> ids) {
        return productCrudService.getProductsByIds(ids);
    }
}
