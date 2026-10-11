package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ProductCategoryService {

    void putProductCategories(Long productId, Set<Integer> categoriesIds);
    Collection<Category> getProductCategories(Long productId);
    Map<Long,Collection<Category>  > getProductsCategories(Set<Long> productIds);

}
