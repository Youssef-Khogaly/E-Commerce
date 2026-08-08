package com.ecommerce.DTO.Requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public record AddProductRequest(@NotBlank @Schema(example = "laptop") String title
        , @Nullable @Schema(example = "laptop  core i7 , 16GB ram") String description ,
                                @NotNull @Positive @Schema(example = "1000", description = "always in cents , USD") Long priceInCents
                             , @NotNull@PositiveOrZero @Schema(example = "5")Integer stock) {
}
