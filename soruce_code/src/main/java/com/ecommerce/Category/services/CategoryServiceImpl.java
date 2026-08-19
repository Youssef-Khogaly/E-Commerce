package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Category.repos.CategoryJpaRepo;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.Exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Set<Category> findAllById(Set<Integer> ids) {
        Set<Category> categories = categoryJpaRepo.findAllById(ids).stream().collect(Collectors.toUnmodifiableSet());
        if(categories.size() != ids.size()){
            Set<Integer>existsIds = categories.stream().map(Category::getCate_id).collect(Collectors.toSet());

            throw new NotFoundException("bad categories id:" + ids.stream().filter(c -> !existsIds.contains(c)).toList() + "doesn't exist");
        }

        return categories;
    }
}
