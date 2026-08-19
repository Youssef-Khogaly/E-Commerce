package com.ecommerce.util;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;


public record ErrorResponse(
        HttpStatus status,
        List<String> message,
        String path,
        Instant timeStamp

) {
}
