package com.ecommerce.DTO.Requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record AddProductRequest(@NotBlank String title , @Nullable String description ,
                                @NotNull @Positive Long priceInCents
                             , @NotNull@PositiveOrZero Integer stock) {
}
