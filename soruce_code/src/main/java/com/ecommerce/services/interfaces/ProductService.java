package com.ecommerce.services.interfaces;

import com.ecommerce.DTO.ProductDTO;
import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;
import com.ecommerce.Exception.NotFoundException;

import com.ecommerce.services.ProductSortByOptions;
import com.ecommerce.services.ProductSortDirection;

import org.springframework.data.domain.Page;

import java.util.*;

;

public interface ProductService  {
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
                                                , long price,
                                               Integer stock
        ){ }
    public enum DeletedOptions{
        SOFT_DELETED_ONLY , INCLUDE_SOFT_DELETED , NON_DELETED
    }
        public static record QueryProduct(int page, int pageSize
                , String name , Long minPrice , Long maxPrice
                , Integer category
                , ProductSortByOptions sortBy, ProductSortDirection direction
        ){}

    public Page<ProductSearchView> getProducts(QueryProduct queryProduct);
    public ProductDTO getProduct(Long product_id);
    public boolean isProductExists(Long product_id);

    Collection<Category> getProductCategory(Long product_id);
    // real deletion are not allowed
    public void deleteProduct(Long product_id) throws NotFoundException;

    public Product addProduct(PostProductCommand command);

    public void updateProduct(UpdateProductCommand commands);

    public void putProductCategories(Long product_id,Set<Integer>categoriesIds);

    public Map<Long,ProductDTO> getProducts(Collection<Long> ids);
}

