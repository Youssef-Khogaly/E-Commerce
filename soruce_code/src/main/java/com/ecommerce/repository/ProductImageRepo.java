package com.ecommerce.repository;

import com.ecommerce.entities.Products.ProductImageId;
import com.ecommerce.entities.Products.ProductImages;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductImageRepo extends CrudRepository<ProductImages, ProductImageId> {


    List<ProductImages> findAllByProduct_Id(long productId);

    void deleteAllByProduct_Id(long productId);
}
