package com.ecommerce.inventory.service;

import java.util.Map;

public interface InventoryService {


    public void add(Map<Long,Integer> id_quantityMap);
    public void remove(Map<Long,Integer> id_quantityMap);

    public void create(Long productId);
    public void create(Long productId, int quantity);
}
