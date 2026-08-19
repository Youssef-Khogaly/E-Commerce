package com.ecommerce.Category.services;

import com.ecommerce.Category.Category;

import java.util.List;

public interface CategoryService {


    List<Category> getAllCategories();
    Category getCategory(Integer id);
    Category updateCategory(Integer id, String name);
    Category addCategory(String name);
    void deleteCategory(Integer id);

}
