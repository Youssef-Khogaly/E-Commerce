package com.ecommerce.inventory.service;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public interface InventoryReservationService {
    public long try_reserve(@NotNull final Map<Long, Integer> id_quantityMap);
    public long reserve(Map<Long,Integer> id_quantityMap );

    public void release(Long reservationId);
    public void commit(Long reservationId);
}
