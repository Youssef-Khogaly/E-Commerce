package com.ecommerce.inventory.service.Impl;

import com.ecommerce.inventory.exceptions.InventoryInsufficientStock;
import com.ecommerce.inventory.infra.entity.Inventory;
import com.ecommerce.inventory.service.InventoryQueryService;
import com.ecommerce.inventory.service.StockPoolRefillService;
import com.ecommerce.inventory.service.repo.AvailablePoolRepo;
import com.ecommerce.inventory.service.repo.InventoryRepo;
import com.ecommerce.util.advices.ReqCollapsing;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class StockPoolRefillImpl implements StockPoolRefillService {
    private final InventoryQueryService inventoryQueryService;
    private final InventoryRepo inventoryRepo;
    private final AvailablePoolRepo availablePoolRepo;


    private void doRefill(Inventory inventory)
    {
        long productId = inventory.getId();
        if(inventory.getTotalQuantity() == 0 ) // fast rollback
            throw new InventoryInsufficientStock("insufficient stock"  ,List.of(productId));

        long t1 = Instant.now().toEpochMilli();
        var meta = inventoryQueryService.getMetaData(productId);
        log.debug("refill aggregation time {} ms" , Instant.now().toEpochMilli() - t1);
        // another transaction already refilled
        if(meta.pooled() >= REFILL_THRESHOLD)
            return;
        if(meta.available() == 0)
            throw new InventoryInsufficientStock("Insufficient stock", List.of(productId));
        int refill = Math.min(meta.unpooled(),MAX_REFILL_QUANTITY);
        long t2 = Instant.now().toEpochMilli();
        availablePoolRepo.fill(productId,refill);
        log.debug("refill batch insert time {} ms" , Instant.now().toEpochMilli() - t2);
    }

    // always run in new transaction, as it hold lock and retry
   // @Retryable(retryFor = PessimisticLockingFailureException.class,noRetryFor = InventoryInsufficientStock.class ,maxAttempts = 5 , backoff = @Backoff(delay = 50,maxDelay = 200,multiplier = 2))

    @ReqCollapsing(keys = "#productId")
    @Transactional(isolation = Isolation.READ_COMMITTED , propagation = Propagation.REQUIRES_NEW)
    @Override
    public void refill(Long productId ) {
        long t1 = Instant.now().toEpochMilli();
        Inventory inventory = inventoryRepo.findByIdForShare(productId);
        try{
            doRefill(inventory);
        }finally {
            log.debug("Refill done, time taken: {} ms" , Instant.now().toEpochMilli() - t1);
        }


        return;

    }

}
