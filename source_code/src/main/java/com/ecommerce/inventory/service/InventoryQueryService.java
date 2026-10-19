package com.ecommerce.inventory.service;

import com.ecommerce.inventory.infra.InventoryMetaDataView;
import com.ecommerce.inventory.infra.ProductAvailableStockView;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface InventoryQueryService {

    InventoryMetaDataView getMetaData(long productId);
    List<InventoryMetaDataView> getMetaData(Set<Long> productIds);
    ProductAvailableStockView getAvailableStockView(long productId);
    List<ProductAvailableStockView> getAvailableStockView(Set<Long> productIds);
}

