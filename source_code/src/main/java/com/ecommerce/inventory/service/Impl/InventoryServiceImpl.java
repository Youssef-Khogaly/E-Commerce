package com.ecommerce.inventory.service.Impl;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Product.services.crud.ProductCrudService;
import com.ecommerce.inventory.exceptions.InventoryInsufficientStock;
import com.ecommerce.inventory.infra.AvailableUnit;
import com.ecommerce.inventory.infra.InventoryMetaDataView;
import com.ecommerce.inventory.infra.entity.Inventory;
import com.ecommerce.inventory.service.InventoryQueryService;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.repo.AvailablePoolRepo;
import com.ecommerce.inventory.service.repo.InventoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service

public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepo inventoryRepo;
    private final InventoryQueryService inventoryQueryService;
    private final AvailablePoolRepo availablePoolRepo;
    private List<Inventory> findAllForUpdate(Set<Long> ids){
        var retList = inventoryRepo.findAllByIdForUpdate(ids);
        if(retList.size() != ids.size()){
            var nonExisting = new HashSet<>(ids);
            retList.forEach(inventory -> {
                nonExisting.remove(inventory.getId());
            });
            throw new NotFoundException("Product inventory ids does not exists , ids:" + nonExisting);
        }
        return retList;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void add(Map<Long, Integer> id_quantityMap) {
        List<Inventory> inventories = findAllForUpdate(id_quantityMap.keySet());
        inventories.forEach(i -> {
            i.setTotalQuantity(i.getTotalQuantity() + id_quantityMap.get(i.getId()));
        });

    }

    private void doRemove(long productId , int quantity, Inventory inventory , InventoryMetaDataView meta ,List<AvailableUnit> availableInPool , List<AvailableUnit> toRemoveFromPool)
    {
        if(quantity <= meta.unpooled())
        {
            inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);
        }
        else{ // unpooled not enough
            // consume all unpooled first
            inventory.setTotalQuantity(inventory.getTotalQuantity() - meta.unpooled());
            int remaining = quantity- meta.unpooled();
            var removedSubList = availableInPool.subList(0,remaining);
            toRemoveFromPool.addAll(removedSubList);
            availableInPool.removeAll(removedSubList);
        }
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void remove(Map<Long, Integer> id_quantityMap) {
        List<Inventory> inventories = findAllForUpdate(id_quantityMap.keySet());
        List<AvailableUnit> allPooledUnis = availablePoolRepo.selectAllForUpdate(id_quantityMap.keySet());
        List<InventoryMetaDataView> metaList = inventoryQueryService.getMetaData(id_quantityMap.keySet());
        Map<Long,List<AvailableUnit>> productId_pool = allPooledUnis.stream().collect(Collectors.groupingBy(AvailableUnit::productId,Collectors.toCollection(ArrayList::new)));
        Map<Long,InventoryMetaDataView> productId_meta = metaList.stream().collect(Collectors.toMap(InventoryMetaDataView::productId, a -> a));

        List<AvailableUnit> toRemoveFromPool = new ArrayList<>();
        List<Long> insufficientStockIds = new ArrayList<>();

        // validate enough stock
        for(var inventory : inventories)
        {
            var id = inventory.getId();
            var quantity = id_quantityMap.get(id);
            var meta = productId_meta.get(id);
            if(quantity > meta.available()) // insufficient stock
            {
                insufficientStockIds.add(id);
            }
        }
        if(!insufficientStockIds.isEmpty())
            throw new InventoryInsufficientStock("Insufficient stock to remove!!", insufficientStockIds);

        for(var inventory : inventories)
        {
            var id = inventory.getId();
            var quantity = id_quantityMap.get(id);
            var meta = productId_meta.get(id);
            var availableList = productId_pool.get(id);

            doRemove(id,quantity,inventory,meta,availableList,toRemoveFromPool);
        }


        if(!toRemoveFromPool.isEmpty())
            availablePoolRepo.deleteAll(toRemoveFromPool);

        return;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void create(Long productId) {
        create(productId,0);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void create(Long productId, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setId(productId);
        inventory.setTotalQuantity(quantity);
        inventoryRepo.save(inventory);
    }
}
