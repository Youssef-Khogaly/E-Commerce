package com.ecommerce.repository;

import com.ecommerce.entities.Carts.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartJpaRepo extends JpaRepository<Cart,Long> {



    @EntityGraph(
            attributePaths = {"cartItemSet","cartItemSet.product","cartItemSet.product.imagesList","cartItemSet.product.imagesList.image"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("""
    select c from Cart c where c.id = :id
        """)
    Optional<Cart> findByIdForCheckOut(Long id);
}
