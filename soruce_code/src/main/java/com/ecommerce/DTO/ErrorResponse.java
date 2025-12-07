package com.ecommerce.DTO;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Date;
import java.util.List;


public record ErrorResponse(
        HttpStatus status,
        List<String> message,
        String path,
        Instant timeStamp

) {
}
