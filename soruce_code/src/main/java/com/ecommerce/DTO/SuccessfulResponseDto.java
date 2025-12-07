package com.ecommerce.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessfulResponseDto<T>(@Nullable T responseData , @Nullable String message , @Nullable Instant timeStamp) {
    public SuccessfulResponseDto(T data, String message) {
        this(data, message, Instant.now());
    }

    public SuccessfulResponseDto(String message, Instant timeStamp) {
        this(null, message, timeStamp);
    }

    public SuccessfulResponseDto(T data) {
        this(data, null, null);
    }

}
