package com.ecommerce.entities.Carts;

import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.user.Customer;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;


@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CartItemId implements Serializable {
    private Long cart_id;
    private Long product_id;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItemId that = (CartItemId) o;
        return Objects.equals(getCart_id(), that.getCart_id()) && Objects.equals(getProduct_id(), that.getProduct_id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCart_id(), getProduct_id());
    }
}
