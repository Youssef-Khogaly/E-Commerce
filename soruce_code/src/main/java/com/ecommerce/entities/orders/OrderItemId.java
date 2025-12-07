package com.ecommerce.entities.orders;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;


@Embeddable
@Setter
@Getter
public class OrderItemId implements Serializable {
    private UUID orderId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(getOrderId(), that.getOrderId()) && Objects.equals(getProductId(), that.getProductId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderId(), getProductId());
    }
}
