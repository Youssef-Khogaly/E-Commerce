package com.ecommerce.DTO;

import com.stripe.param.checkout.SessionCreateParams;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {

    private ProductDTO productDTO;
    private int quantity;
    private long subTotalInCents;
}
