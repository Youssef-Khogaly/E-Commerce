package com.ecommerce.services.StockService;

public class OutOfStock extends StockException {
    public OutOfStock(String message) {
        super(message);
    }
}
