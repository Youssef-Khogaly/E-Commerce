package com.ecommerce.inventory.service.repo;

import com.ecommerce.inventory.infra.InventoryMetaDataView;
import com.ecommerce.inventory.infra.ProductAvailableStockView;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InventoryMetaDataRepo {

    public Optional<InventoryMetaDataView> findByProductId(long id);
    public List<InventoryMetaDataView> findAllByProductIds(Set<Long> ids);

    public List<ProductAvailableStockView> findAllByProductIdsAvailableView(Set<Long> ids);
}
