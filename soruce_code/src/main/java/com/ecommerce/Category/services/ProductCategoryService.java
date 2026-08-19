package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;

import java.util.Map;
import java.util.Set;

public interface ProductCategoryService {

    void putProductCategories(Long productId, Set<Integer> categoriesIds);
    Set<Category> getProductCategories(Long productId);
    Map<Long,Set<Category>  > getProductsCategories(Set<Long> productIds);

}
