package com.ecommerce.repository;

import com.ecommerce.entities.Carts.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartJpaRepo extends JpaRepository<Cart,Long> {

    List<Cart> getById(long id);
}
