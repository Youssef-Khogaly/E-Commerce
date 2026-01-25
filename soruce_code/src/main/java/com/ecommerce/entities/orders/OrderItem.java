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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "order_id" , columnDefinition = "BINARY(16)")
    private Order order;
    private Long product_id;

    private int quantity;
    private Long unitPriceInCents;
    private Long discountInCents;
    private Long subTotalInCents;
    @Column(name = "currency_code")
    private String currencyCode;
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
