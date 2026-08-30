package com.ecommerce.Images.dtos;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record ProductImageDto(@Positive Long id, @NotEmpty String url, Boolean isMain) implements Serializable {
}
