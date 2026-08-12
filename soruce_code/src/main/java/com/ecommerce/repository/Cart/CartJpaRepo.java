package com.ecommerce.repository.Cart;

import com.ecommerce.entities.Carts.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CartJpaRepo extends CrudRepository<Cart,Long> {



}
