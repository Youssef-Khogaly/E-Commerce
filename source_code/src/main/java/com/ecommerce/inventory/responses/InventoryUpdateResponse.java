package com.ecommerce.inventory.responses;

import lombok.Builder;

@Builder
public record InventoryUpdateResponse(long productId , InventoryUpdateOperation operation , int affectedQuantity)  {
}

