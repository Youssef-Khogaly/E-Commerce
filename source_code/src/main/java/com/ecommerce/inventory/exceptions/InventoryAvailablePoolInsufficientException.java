package com.ecommerce.inventory.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class InventoryAvailablePoolInsufficientException extends RuntimeException {
    private final Map<Long,Integer> id_required_map;
    public InventoryAvailablePoolInsufficientException(String message, Map<Long,Integer> id_required_map) {
        super(message);
        this.id_required_map = id_required_map;
    }
}
