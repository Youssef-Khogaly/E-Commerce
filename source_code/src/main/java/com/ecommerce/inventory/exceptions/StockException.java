package com.ecommerce.inventory.exceptions;

public class StockException extends RuntimeException {
    public StockException(String message) {
        super(message);
    }
}
