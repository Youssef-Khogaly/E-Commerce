package com.ecommerce.entities.Carts;

import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "cart_item")
public class CartItem {

    @EmbeddedId
    private CartItemId id = new CartItemId();

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @MapsId("cart_id")
    private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY , optional = false)
    @JoinColumn(name = "prod_id")
    @MapsId("product_id")
    private Product product;
    @PositiveOrZero
    private  int quantity;



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(getId(), cartItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}