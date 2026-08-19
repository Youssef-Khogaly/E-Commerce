package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Category.entity.ProductCategory;
import com.ecommerce.Category.repos.CategoryJpaRepo;
import com.ecommerce.Category.repos.ProductCategoryRepo;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{
    private ProductService productService;
    private CategoryService categoryService;
    private ProductCategoryRepo productCategoryRepo;
    @Transactional
    @Override
    public void putProductCategories(final Long productId, final Set<Integer> categoriesIds) {
        if(!productService.isProductExists(productId)){
            throw new NotFoundException("product with id:" + productId +"doesn't exists or soft deleted");
        }
        final Product product = productService.getReferenceById(productId);

        if(categoriesIds.isEmpty()){
            productCategoryRepo.deleteAllByProductId(productId);
            return;
        }
        Set<Category> categories = categoryService.findAllById(categoriesIds);

        productCategoryRepo.deleteAllByProductId(productId);
        productCategoryRepo.flush(); // to not throw db constrains we insert product category that was exists before

        List<ProductCategory> productCategoryList = categories.stream().map(c ->{
            ProductCategory productCategory = new ProductCategory(productId,c.getCate_id());
            productCategory.setProduct(product);
            productCategory.setCategory(c);
            return productCategory;
        }).toList();

        productCategoryRepo.saveAll(productCategoryList);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Category> getProductCategories(Long productId) {
        return productCategoryRepo.findByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Set<Category>> getProductsCategories(Set<Long> productIds){
        if(productIds == null || productIds.isEmpty())
            return Map.of();

        Set<ProductCategory> productCategories = productCategoryRepo.findAllByProductIds(productIds);
        if(productCategories.isEmpty())
            return Map.of();

        return productCategories.stream().collect(Collectors.groupingBy(ProductCategory::getProductId,
                                                                Collectors.mapping(ProductCategory::getCategory,Collectors.toSet())));
    }
}
