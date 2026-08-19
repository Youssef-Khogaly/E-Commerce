package com.ecommerce.Product.services;

import com.ecommerce.Images.resposes.ProductImageResponseDto;
import com.ecommerce.Category.entity.Category;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.entity.ProductSortByOptions;
import com.ecommerce.Product.entity.ProductSortDirection;
import com.ecommerce.Product.dtos.ProductSearchView;
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
                                                , long price
        ){ }
    public enum DeletedOptions{
        SOFT_DELETED_ONLY , INCLUDE_SOFT_DELETED , NON_DELETED
    }
        public static record QueryProduct(int page, int pageSize
                , String name , Long minPrice , Long maxPrice
                , Integer category
                , ProductSortByOptions sortBy, ProductSortDirection direction
        ){}

    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct);
    public Product getProductById(Long product_id);
    public boolean isProductExists(Long product_id);

    Collection<Category> getProductCategory(Long product_id);
    // real deletion are not allowed
    public void deleteProduct(Long product_id) ;

    public Product addProduct(PostProductCommand command);

    public void updateProduct(UpdateProductCommand commands);

    public void putProductCategories(Long product_id,Set<Integer>categoriesIds);

    public Map<Long,ProductSearchView> getProductSearchView(Collection<Long> ids);

    public void putProductImages(Long productId,Set<Long> imageIds);
    public List<ProductImageResponseDto> getProductImages(Long ProductId);
    public void setProductMainImage(Long productId , Long mainImageId);

    public Product getReferenceById(Long id);

    public Product save(Product product);
}

