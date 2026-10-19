package com.ecommerce.inventory.infra;

import lombok.Builder;


@Builder
public record ProductAvailableStockView(long productId, int totalQuantity , int availableStock) {
}
