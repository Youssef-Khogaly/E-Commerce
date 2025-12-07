package com.ecommerce.entities.orders;

import com.ecommerce.entities.Products.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;



@Entity
@Table(name = "order_item")
@Getter@Setter
public class OrderItem {

    @EmbeddedId
    private OrderItemId id = new OrderItemId();

    @MapsId(value = "orderId")
    @ManyToOne
    @JoinColumn(name = "order_id" , columnDefinition = "BINARY(16)")
    private Order order;

    @MapsId(value = "productId")
    @ManyToOne(fetch = FetchType.LAZY , optional = true)
    @JoinColumn(name = "product_id_reference")
    private Product product;
    private int quantity;
    private long unitPriceInCents;
    private long discountInCents;
    private long subTotalInCents;
    private String name;
    private String description;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem item = (OrderItem) o;
        return Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
