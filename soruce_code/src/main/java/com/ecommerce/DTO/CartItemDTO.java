package com.ecommerce.DTO;

import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
