package com.ecommerce.orders.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
