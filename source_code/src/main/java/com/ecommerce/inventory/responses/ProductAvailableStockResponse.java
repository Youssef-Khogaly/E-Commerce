package com.ecommerce.inventory.responses;

import com.ecommerce.inventory.infra.ProductAvailableStockView;
import lombok.Builder;

@Builder
public record ProductAvailableStockResponse(long productId, int availableQuantity) {

    public static ProductAvailableStockResponse from (ProductAvailableStockView v){
        return ProductAvailableStockResponse.builder()
                .productId(v.productId()).availableQuantity(v.availableStock()).build();
    }
}
