package com.ecommerce.Cart.repos;

import com.ecommerce.Cart.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartJpaRepo extends CrudRepository<Cart,Long> {



}
