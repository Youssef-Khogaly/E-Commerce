package com.ecommerce.Product.services.crud;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.repos.ProductJpaRepo;
import com.ecommerce.Stock.service.IStockService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductCrudService {


    private final ProductJpaRepo productJpaRepo;
    private final IStockService stockService;

    @Override
    public Product getProductById(Long product_id) {

        return productJpaRepo.findById(product_id).orElseThrow(
                ()-> new NotFoundException("product with id:" +product_id +" doesn't exists")
        );
    }

    @Override
    public Map<Long, Product> getProductsByIds(Set<Long> ids) {
        List<Product> products = productJpaRepo.findAllByIdReadOnly(ids);
        if(ids.size() != products.size())
        {
            Set<Long> existingIds = products.stream().map(Product::getId).collect(Collectors.toSet());
            List<Long> nonExisitng = ids.stream().filter(i -> !existingIds.contains(i)).toList();
            throw new NotFoundException("product ids does not exists: " + nonExisitng);
        }

        return products.stream().collect(Collectors.toMap(Product::getId,product -> product));
    }

    public Product getReferenceById(Long id)
    {
        return productJpaRepo.getReferenceById(id);
    }
    @Caching(
            evict = {
                    @CacheEvict(value = CACHE_NAME,key = "#product.id"),
            }


    )
    public Product save(Product product)
    {
        return productJpaRepo.save(product);
    }
    @Override
    public boolean isProductExists(Long product_id) {
        return productJpaRepo.isExists(product_id);
    }

    @Override
    public void existsById(Long productId) {
        if(!isProductExists(productId))
            throw new NotFoundException("Product " + productId + " does not exist");
    }

    @Override
    public void existsByIds(Set<Long> productIds) {
        if(productIds == null || productIds.isEmpty())
            return;

        var exists = productJpaRepo.getExistingIds(productIds);
        if(exists.size() != productIds.size())
        {
            List<Long> notExistingIds = productIds.stream().filter(i -> !exists.contains(i)).toList();
            throw new NotFoundException("prodcut ids: " + notExistingIds + " does not exists");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CACHE_NAME,key = "#product_id"),
            }


    )
    public void deleteProduct(Long product_id) {
        if(!productJpaRepo.existsById(product_id))
            throw new NotFoundException("product with id:" +product_id +" doesn't exists");

        productJpaRepo.deleteById(product_id);

    }

    @Override
    @Transactional
    public Product addProduct(PostProductCommand command) {
        Product product = new Product();

        product.setTitle(command.title());
        product.setPrice(command.price());
        product.setDescription(command.description());
        product =  productJpaRepo.save(product);

        stockService.create(product.getId(),command.stock());

        return product;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CACHE_NAME,key = "#command.product_id")
    public void updateProduct(UpdateProductCommand command){


        Product product= productJpaRepo.findByIdForUpdate(command.product_id()).orElseThrow(
                () -> new NotFoundException("product with id:" + command.product_id()+" doesn't exists"));
        product.setTitle(command.title());
        product.setDescription(command.description());
        product.setPrice(command.price());
    }



}
