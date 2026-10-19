package com.ecommerce.inventory.infra;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReservedUnit(
        Long id, Long operationId, Long productId, Integer quantity, Instant expireAt
        ) {

    public boolean isExpired(){
        return Instant.now().isAfter(expireAt());
    }
    public boolean isExpired(Instant now){
        return now.isAfter(expireAt());
    }
}
