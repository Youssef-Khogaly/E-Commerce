package com.ecommerce.Category.repos;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Category.entity.ProductCategory;
import com.ecommerce.Category.entity.ProductCategoryId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ProductCategoryRepo extends JpaRepository<ProductCategory, ProductCategoryId> {


    @Query(
            """
            select c from ProductCategory pc inner join Category c on pc.product = :productId and pc.category.cate_id = c.cate_id
            """
    )
    Set<Category> findByProductId(Long productId);

    @EntityGraph(attributePaths = {"category"},type = EntityGraph.EntityGraphType.FETCH)
    @Query("select pc from ProductCategory pc where pc in :productIds")
    Set<ProductCategory> findAllByProductIds(Set<Long> productIds);

    void deleteAllByProductId(Long productId);


}
