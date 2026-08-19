package com.ecommerce.Images.repos;

import com.ecommerce.Images.entity.ProductImageId;
import com.ecommerce.Images.entity.ProductImages;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductImageRepo extends CrudRepository<ProductImages, ProductImageId> {


    List<ProductImages> findAllByProduct_Id(long productId);

    void deleteAllByProduct_Id(long productId);
}
