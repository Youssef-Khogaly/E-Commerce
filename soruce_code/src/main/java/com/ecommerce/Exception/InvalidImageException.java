package com.ecommerce.Exception;

import jakarta.validation.ValidationException;

public class InvalidImageException extends ValidationException {
    public InvalidImageException(String message) {
        super(message);
    }
}
