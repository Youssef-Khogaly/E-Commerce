package com.ecommerce.inventory.service.repo;

import com.ecommerce.inventory.infra.AvailableUnit;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AvailablePoolRepo {

    List<AvailableUnit> selectForUpdateSkipLocked(final long productId, final int quantity);
    // reserved units grouped by p
    List<AvailableUnit> selectAllForUpdateSkipLocked(final Map<Long,Integer>productId_quantity);
    List<AvailableUnit> selectAllForShare(Collection<Long>productIds);
    List<AvailableUnit> selectAllForUpdate(Collection<Long> productIds);
    void deleteAll(final Collection<AvailableUnit> ids);

    void fill(final long productId , int quantity);
    void fill(final Map<Long,Integer> productId_quantity);

}
