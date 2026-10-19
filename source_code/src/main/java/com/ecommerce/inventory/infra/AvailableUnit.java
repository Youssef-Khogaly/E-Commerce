package com.ecommerce.inventory.infra;

import lombok.Builder;

@Builder
public record AvailableUnit(Long id, Long productId) {
}
