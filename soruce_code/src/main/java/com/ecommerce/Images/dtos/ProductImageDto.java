package com.ecommerce.Images.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ProductImageDto(@Positive Long id, @NotEmpty String url, boolean isMain){
}
