package com.ecommerce.inventory.exceptions;

public class OutOfStock extends StockException {
    public OutOfStock(String message) {
        super(message);
    }
}
