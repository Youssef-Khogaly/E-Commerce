package com.ecommerce.DTO.Requests;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PutProductRequest(
        @NotNull @NotBlank String title ,
        @NotNull @NotBlank String description ,
        @NotNull @Positive Long priceInCents ,
        @NotNull@PositiveOrZero Integer stock
){
}