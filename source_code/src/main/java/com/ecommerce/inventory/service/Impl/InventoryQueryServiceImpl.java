package com.ecommerce.inventory.service.Impl;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.inventory.infra.InventoryMetaDataView;
import com.ecommerce.inventory.infra.ProductAvailableStockView;
import com.ecommerce.inventory.service.InventoryQueryService;
import com.ecommerce.inventory.service.repo.AvailablePoolRepo;
import com.ecommerce.inventory.service.repo.InventoryMetaDataRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class InventoryQueryServiceImpl implements InventoryQueryService {
    private final InventoryMetaDataRepo inventoryMetaDataRepo;
    private final AvailablePoolRepo availablePoolRepo;
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InventoryMetaDataView getMetaData(long productId) {
        return inventoryMetaDataRepo.findByProductId(productId).orElseThrow(() -> new NotFoundException("product id: "+ productId+" does not exists"));

    }
    private void validateIdSetInput(Set<Long> productIds){
        if(productIds == null)
            throw new NullPointerException("null ids collection, cannot generate inventory metadata query!!");
        if(productIds.isEmpty())
            throw new IllegalArgumentException("Empty collection, cannot generate inventory metadata query!!");
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<InventoryMetaDataView> getMetaData(Set<Long> productIds) {
        validateIdSetInput(productIds);

        var ret = inventoryMetaDataRepo.findAllByProductIds(productIds);
        if(ret.size() != productIds.size())
        {
            var nonExistingIds = new HashSet<>(productIds);
            ret.forEach(item -> nonExistingIds.remove(item.productId()));
            throw new NotFoundException("product ids: " + nonExistingIds + " does not exists!!");
        }
        return ret;
    }


    @Override
    public ProductAvailableStockView getAvailableStockView(long productId) {

        var ret = inventoryMetaDataRepo.findAllByProductIdsAvailableView(Set.of(productId));
        if(ret.isEmpty()){
            throw new NotFoundException("product id: " + productId + " does not exists");
        }

        return ret.get(0);
    }

    @Override
    public List<ProductAvailableStockView> getAvailableStockView(Set<Long> productIds) {
        validateIdSetInput(productIds);
        var ret = inventoryMetaDataRepo.findAllByProductIdsAvailableView(productIds);
        if(ret.size() != productIds.size())
        {
            var nonExistingIds = new HashSet<>(productIds);
            ret.forEach(item -> nonExistingIds.remove(item.productId()));
            throw new NotFoundException("product ids: " + nonExistingIds + " does not exists!!");
        }
        return ret;
    }
}
