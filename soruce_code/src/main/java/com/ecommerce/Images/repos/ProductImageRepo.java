package com.ecommerce.Images.repos;

import com.ecommerce.Images.entity.ProductImageId;
import com.ecommerce.Images.entity.ProductImages;
import com.ecommerce.Product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface ProductImageRepo extends JpaRepository<ProductImages, ProductImageId> {


    List<ProductImages> findAllByProduct_Id(long productId);


    @EntityGraph(attributePaths = "image",type = EntityGraph.EntityGraphType.FETCH)
    @Query(
            """
            select i from ProductImages i where i.productImageId.product_id in :productIds
            """
    )
    List<ProductImages>findAllByProductIds(Set<Long> productIds);
    void deleteAllByProduct_Id(long productId);
}
