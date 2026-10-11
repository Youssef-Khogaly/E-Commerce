package com.ecommerce.Exception;

public class NotFoundException extends ServiceLayerException {
    public NotFoundException(String message) {
        super(message);
    }
}
