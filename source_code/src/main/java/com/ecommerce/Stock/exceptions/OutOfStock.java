package com.ecommerce.Stock.exceptions;

public class OutOfStock extends StockException {
    public OutOfStock(String message) {
        super(message);
    }
}
