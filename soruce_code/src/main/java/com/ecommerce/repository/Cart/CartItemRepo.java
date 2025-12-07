package com.ecommerce.repository.Cart;

import com.ecommerce.entities.Carts.CartItem;
import com.ecommerce.entities.Carts.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepo extends JpaRepository<CartItem, CartItemId> {


}
