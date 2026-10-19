package com.ecommerce.inventory.service.Impl;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.inventory.exceptions.InventoryAvailablePoolInsufficientException;
import com.ecommerce.inventory.exceptions.InventoryInsufficientStock;
import com.ecommerce.inventory.exceptions.InventoryOverSellingException;
import com.ecommerce.inventory.exceptions.InventoryReservationNotFound;
import com.ecommerce.inventory.infra.*;
import com.ecommerce.inventory.service.InventoryReservationService;
import com.ecommerce.inventory.service.StockPoolRefillService;
import com.ecommerce.inventory.service.repo.AvailablePoolRepo;
import com.ecommerce.inventory.service.repo.InventoryMetaDataRepo;
import com.ecommerce.inventory.service.repo.InventoryRepo;
import com.ecommerce.inventory.service.repo.ReservedPoolRepo;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryReservationImpl implements InventoryReservationService {
    private final ReservedPoolRepo reservedPoolRepo;
    private final AvailablePoolRepo availablePoolRepo;
    private final InventoryRepo inventoryRepo;
    private final StockPoolRefillService stockPoolRefillService;
    private final InventoryMetaDataRepo inventoryMetaDataRepo;
    private final TransactionTemplate transactionTemplate;

    public InventoryReservationImpl(ReservedPoolRepo reservedPoolRepo, AvailablePoolRepo availablePoolRepo, InventoryRepo inventoryRepo, StockPoolRefillService stockPoolRefillService, InventoryMetaDataRepo inventoryMetaDataRepo, PlatformTransactionManager platformTransactionManager) {
        this.reservedPoolRepo = reservedPoolRepo;
        this.availablePoolRepo = availablePoolRepo;
        this.inventoryRepo = inventoryRepo;
        this.stockPoolRefillService = stockPoolRefillService;
        this.inventoryMetaDataRepo = inventoryMetaDataRepo;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    // return map of product id to required quantity to be sufficient
    private Map<Long,Integer> checkReserveSufficient(final Map<Long,List<AvailableUnit>> productId_reservedUnits , final Map<Long,Integer> id_quantityMap)
    {
        final Map<Long,Integer> ret = new HashMap<>();
        id_quantityMap.forEach(
                (id, quantity) ->{
                    int required = quantity - productId_reservedUnits.getOrDefault(id,Collections.emptyList()).size();
                    if(required > 0)
                        ret.put(id,required);
                }
        );

        return ret;
    }
    public long try_reserve(@NotNull final Map<Long, Integer> id_quantityMap){
        final var units = availablePoolRepo.selectAllForUpdateSkipLocked(id_quantityMap);
        if(units.isEmpty()){
            throw new InventoryAvailablePoolInsufficientException("insufficient stock in the pool ",id_quantityMap);
        }
        final var id_availableUnitMap = units.stream()
                .collect(Collectors.groupingBy(AvailableUnit::productId, Collectors.toList()));

        var requiredMap = checkReserveSufficient(id_availableUnitMap,id_quantityMap);
        if(!requiredMap.isEmpty()){ // in sufficient stock, throw exception to trigger refill
            throw new InventoryAvailablePoolInsufficientException("insufficient stock in the pool ",requiredMap);
        }
        availablePoolRepo.deleteAll(units);

        return reservedPoolRepo.reserve(id_quantityMap, Instant.now().plus(Duration.ofMinutes(20)));
    }

    @Override
    public long reserve(@NotNull final Map<Long, Integer> id_quantityMap) {
        var existingIds = inventoryRepo.getExists(id_quantityMap.keySet());
        if(existingIds.size() != id_quantityMap.size())
        {
            var nonExisting = new HashSet<>(id_quantityMap.keySet());
            existingIds.forEach(nonExisting::remove);
            throw new NotFoundException("product ids does not exist: " + nonExisting);
        }
        try{
            //return transactionTemplate.execute((staus ) ->);
            return  try_reserve(id_quantityMap);
        }catch (InventoryAvailablePoolInsufficientException e) {
            List<Long> insufficientStockIds = new ArrayList<>();
            for(var id : e.getId_required_map().keySet()){

                try{
                    stockPoolRefillService.refill(id);
                } catch (InventoryInsufficientStock ex) {
                    insufficientStockIds.addAll(ex.getProductIds());
                }

            }
            if(!insufficientStockIds.isEmpty())
                throw new InventoryInsufficientStock("Insufficient stock" ,insufficientStockIds );
//             refill successfully , try again
            try{
                return  try_reserve(id_quantityMap);
            }catch (InventoryAvailablePoolInsufficientException ex){

                throw new InventoryInsufficientStock("Insufficient stock", e.getId_required_map().keySet() );
            }
        }
//        return transactionTemplate.execute(status -> {
//                try{
//                    return try_reserve(id_quantityMap);
//                }catch (InventoryAvailablePoolInsufficientException e1) {
//                    List<Long> insufficientStockIds = new ArrayList<>();
//                    for(var id : e1.getId_required_map().keySet()){
//                        try{
//                            stockPoolRefillService.refill(id);
//                        } catch (InventoryInsufficientStock ex) {
//                            insufficientStockIds.addAll(ex.getProductIds());
//                        }
//                    }
//                    if(!insufficientStockIds.isEmpty())
//                        throw new InventoryInsufficientStock("Insufficient stock" ,insufficientStockIds );
//                    // refill successfully , try again
//                    try{
//                        return try_reserve(id_quantityMap);
//                    }catch (InventoryAvailablePoolInsufficientException ex){
//
//                        throw new InventoryInsufficientStock("Insufficient stock", ex.getId_required_map().keySet() );
//                    }
//                }
//            });

    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void release(Long reservationId) {
        reservedPoolRepo.deleteAllByOperation(reservationId);
        return ;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void commit(Long reservationId) {
        var reservedList = reservedPoolRepo.selectByOperationIdForUpdate(reservationId);
        if(reservedList.isEmpty())
            throw new InventoryReservationNotFound("Reservation id:" + reservationId + " does not exists!! cannot commit stock");
        final var productId_reservation = reservedList.stream().collect(Collectors.toMap(ReservedUnit::productId,r -> r));

        final var productInventoryList = inventoryRepo.findAllByIdForUpdate(productId_reservation.keySet());
        reservedPoolRepo.deleteAllByOperation(reservationId);
        productInventoryList.forEach(
                i ->{
                    int newQuantity = i.getTotalQuantity() - productId_reservation.get(i.getId()).quantity();
                    if(newQuantity < 0)
                        throw new InventoryOverSellingException("Unexpected over selling exception , " + " reservedPoolId:" + productId_reservation.get(i.getId()).id());
                    i.setTotalQuantity(newQuantity);
                }
        );

        return ;

    }
}
