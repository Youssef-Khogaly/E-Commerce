package com.ecommerce.inventory.exceptions;

public class InventoryOverSellingException extends InventoryException {
    public InventoryOverSellingException(String message) {
        super(message);
    }
}
