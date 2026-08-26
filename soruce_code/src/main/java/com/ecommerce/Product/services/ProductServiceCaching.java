package com.ecommerce.Product.services;

import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.Product;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Map;
import java.util.Set;



@Primary
public class ProductServiceCaching implements ProductService{

    private final ProductService productService;

    public ProductServiceCaching(@Qualifier("productServiceImpl") ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct) {
        return productService.getProductSearchView(queryProduct);
    }

    @Override
    @Cacheable(value = CACHE_NAME,key = "#product_id")
    public Product getProductById(Long product_id) {
        return productService.getProductById(product_id);
    }

    @Override
    public boolean isProductExists(Long product_id) {
        return productService.isProductExists(product_id);
    }

    @Override
    public void existsById(Long productId) {
            productService.existsById(productId);
    }

    @Override
    public void existsByIds(Set<Long> productIds) {
        productService.existsByIds(productIds);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CACHE_NAME,key = "#product_id"),
            }


    )
    public void deleteProduct(Long product_id) {
        productService.deleteProduct(product_id);
    }

    @Override
    public Product addProduct(PostProductCommand command) {
        return productService.addProduct(command);
    }

    @Override
    @CacheEvict(value = CACHE_NAME,key = "#command.product_id")
    public void updateProduct(UpdateProductCommand command) {
        productService.updateProduct(command);
    }

    @Override
    public Map<Long, ProductSearchView> getProductSearchView(Collection<Long> ids) {
        return productService.getProductSearchView(ids);
    }

    @Override
    public Product getReferenceById(Long id) {
        return productService.getReferenceById(id);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CACHE_NAME,key = "#product.id"),
            }


    )
    public Product save(Product product) {
        return productService.save(product);
    }
}
