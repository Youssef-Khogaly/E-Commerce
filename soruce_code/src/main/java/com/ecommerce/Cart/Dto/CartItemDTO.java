package com.ecommerce.Cart.Dto;

import com.ecommerce.util.Money;
import com.ecommerce.Product.dtos.ProductSearchView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private ProductSearchView productDTO;
    private int quantity;
    private Money subTotalInCents;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CartItemDTO that)) return false;
        return getQuantity() == that.getQuantity() && Objects.equals(getProductDTO(), that.getProductDTO());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductDTO(), getQuantity());
    }
}
