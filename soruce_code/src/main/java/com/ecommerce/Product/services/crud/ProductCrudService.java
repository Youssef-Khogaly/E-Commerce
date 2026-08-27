package com.ecommerce.Product.services.crud;

import com.ecommerce.Product.entity.Product;

import java.util.*;

;

public interface ProductCrudService {

    public static final String CACHE_NAME = "products";
    // fields that you don't want to update set it null


        public static record PostProductCommand(
                String title ,
                String description ,
                long price ,
                Integer stock
        ){
        }
        public static record UpdateProductCommand(
                Long product_id,
                                                String title ,
                                               String description
                                                , long price
        ){ }
    public enum DeletedOptions{
        SOFT_DELETED_ONLY , INCLUDE_SOFT_DELETED , NON_DELETED
    }



    public Product getProductById(Long product_id);
    public Map<Long,Product>getProductsByIds(Set<Long>ids);
    public boolean isProductExists(Long product_id);
    public void existsById(Long productId);
    public void existsByIds(Set<Long>productIds);

    // real deletion are not allowed
    public void deleteProduct(Long product_id) ;

    public Product addProduct(PostProductCommand command);

    public void updateProduct(UpdateProductCommand commands);





    public Product getReferenceById(Long id);

    public Product save(Product product);
}

