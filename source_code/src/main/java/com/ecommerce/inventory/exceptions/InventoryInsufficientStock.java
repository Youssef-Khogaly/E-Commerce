package com.ecommerce.inventory.exceptions;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class InventoryOutOfStock extends InventoryException {
    private Collection<Long> productIds;

    public InventoryOutOfStock(String message, Collection<Long> productIds) {
        super(message);
        this.productIds = productIds;
    }
}
