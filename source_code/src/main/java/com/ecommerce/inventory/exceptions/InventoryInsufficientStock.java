package com.ecommerce.inventory.exceptions;

import lombok.Getter;

import java.util.Collection;

@Getter
public class InventoryInsufficientStock extends InventoryException {
    private Collection<Long> productIds;

    public InventoryInsufficientStock(String message, Collection<Long> productIds) {
        super(message);
        this.productIds = productIds;
    }
}
