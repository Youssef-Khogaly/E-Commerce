package com.ecommerce.services.interfaces;

import com.ecommerce.entities.Categories.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {


    List<Category> getAllCategories();
    Category getCategory(Integer id);
    Category updateCategory(Integer id, String name);
    Category addCategory(String name);
    void deleteCategory(Integer id);

}
