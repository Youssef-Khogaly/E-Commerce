package com.ecommerce.Controllers;

import com.ecommerce.entities.Categories.Category;
import com.ecommerce.services.interfaces.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Validated
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;


    @GetMapping
    public ResponseEntity<Collection<Category>> getCategory(){

        var ret = categoryService.getAllCategories();


        return ResponseEntity.status(HttpStatus.OK).body(ret);
    }
    @PostMapping
        public ResponseEntity<Category> addCategory(@Valid @RequestParam  @NotNull @NotBlank String name){
        Category ret = categoryService.addCategory(name);
        return ResponseEntity.created(URI.create("/api/categories/" + ret.getCate_id())).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Integer id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();

    }
}
