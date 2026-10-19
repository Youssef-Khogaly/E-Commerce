package com.ecommerce.inventory.infra;

import lombok.Builder;

import java.util.Objects;

@Builder
public record InventoryMetaDataView(long productId, int totalStock, int reserved, int pooled){

    public int available(){
        return totalStock() - reserved();
    }
    public int unpooled(){
        return available() - pooled();
    }
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InventoryMetaDataView that)) return false;
        return productId() == that.productId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId());
    }
}
