package com.ecommerce.Product.requests;

import jakarta.validation.constraints.*;

public record PutProductRequest(
        @NotNull @NotBlank String title ,
        @NotNull @NotBlank String description ,
        @NotNull @Positive Long priceInCents
){
}