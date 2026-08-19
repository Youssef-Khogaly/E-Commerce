package com.ecommerce.Product.repos;

import com.ecommerce.Product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductQueryRepo extends JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(
            attributePaths = {"stock","categories","images"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
