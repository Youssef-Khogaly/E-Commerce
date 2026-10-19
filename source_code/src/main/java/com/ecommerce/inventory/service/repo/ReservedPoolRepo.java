package com.ecommerce.inventory.service.repo;

import com.ecommerce.inventory.infra.ReservedUnit;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReservedPoolRepo {

    List<ReservedUnit> selectByOperationIdForUpdate(Long operationId);
    List<ReservedUnit> selectAllExpiredForUpdate(int limit);
    void deleteExpired();
    void deleteAllByOperation(long operationId);

    // return operation id
    long reserve(Map<Long,Integer> productQuantityMap, Instant expireAt);
}
