package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {


    List<Category> getAllCategories();
    Category getCategory(Integer id);
    Category updateCategory(Integer id, String name);
    Category addCategory(String name);
    void deleteCategory(Integer id);

    Set<Category> findAllById(Set<Integer>ids);

}
