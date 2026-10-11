package com.ecommerce.Stock.dtos;

import com.ecommerce.Stock.entity.ProductStock;
import lombok.Builder;

import java.util.Objects;

@Builder
public record StockResponseDto(Long productId , Integer availableStock) {


    public static StockResponseDto from(ProductStock stock)
    {
        return new StockResponseDto(stock.getId(),stock.getAvailableStock());
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StockResponseDto stockResponseDto)) return false;
        return Objects.equals(productId(), stockResponseDto.productId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId());
    }
}
