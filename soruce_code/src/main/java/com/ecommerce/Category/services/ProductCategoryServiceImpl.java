package com.ecommerce.Category.services;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Category.entity.ProductCategory;
import com.ecommerce.Category.repos.ProductCategoryRepo;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.services.crud.ProductCrudService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService{
    private ProductCrudService productCrudService;
    private CategoryService categoryService;
    private ProductCategoryRepo productCategoryRepo;

    @Transactional
    @Override
    public void putProductCategories(final Long productId, final Set<Integer> categoriesIds) {
        if(!productCrudService.isProductExists(productId)){
            throw new NotFoundException("product with id:" + productId +"doesn't exists or soft deleted");
        }
        final Product product = productCrudService.getReferenceById(productId);

        if(categoriesIds.isEmpty()){
            productCategoryRepo.deleteAllByProduct_Id(productId);
            return;
        }
        Set<Category> categories = categoryService.findAllById(categoriesIds);

        productCategoryRepo.deleteAllByProduct_Id(productId);
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
    public Map<Long, Collection<Category>> getProductsCategories(Set<Long> productIds){
        if(productIds == null || productIds.isEmpty())
            return Map.of();

        Set<ProductCategory> productCategories = productCategoryRepo.findAllByProductIds(productIds);
        if(productCategories.isEmpty())
            return Map.of();

        return productCategories.stream().collect(Collectors.groupingBy(ProductCategory::getProductId,
                                                                Collectors.mapping(ProductCategory::getCategory,Collectors.toCollection(ArrayList::new))));
    }
}
