package com.ecommerce.services;

import com.ecommerce.Exception.ConflictException;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.repository.Category.CategoryJpaRepo;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.services.interfaces.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private CategoryJpaRepo categoryJpaRepo;
    @Override
    public List<Category> getAllCategories() {

        return categoryJpaRepo.findAll();
    }

    @Override
    public Category getCategory(Integer id) {

        return categoryJpaRepo.findById(id).orElseThrow(() -> new NotFoundException("Category does not exist , id:" + id));
    }

    @Override
    @Transactional
    public Category updateCategory(Integer id, String name) {
        Category cat =  categoryJpaRepo.findById(id).orElseThrow(() -> new NotFoundException("Category does not exist , id:" + id));
        cat.setName(name);
        return cat;
    }

    @Override
    public Category addCategory(String name) {
        if(categoryJpaRepo.existsByName(name)){
            throw new ConflictException("category " + name + " already exists");
        }
        Category cat = new Category();
        cat.setName(name);
        categoryJpaRepo.exists(Example.of(cat));
        return categoryJpaRepo.save(cat);
    }

    @Override
    public void deleteCategory(Integer id) {
        categoryJpaRepo.deleteById(id);
    }
}
