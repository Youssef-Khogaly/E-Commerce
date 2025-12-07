package com.ecommerce.entities.Products;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*

create table if not exists product_stock(
    product_id bigint primary key ,
    stock INT  not null DEFAULT 0 CHECK (stock >= 0),
    reservedStock INT not null default 0 check ( reservedStock >= 0),
    availableStock int  generated always as (stock-reservedStock)virtual,
    constraint frg_p foreign key (product_id) references product(product_id)
);
 */
@Entity
@Table(name = "product_stock")
@AllArgsConstructor @NoArgsConstructor
@Getter@Setter
public class ProductStock {

    @Id
    private long product_id;

    @JoinColumn(name = "product_id",nullable = false,unique = true)
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    private Product product;
    @PositiveOrZero
    private int stock;
    @PositiveOrZero
    private int reservedStock;
    @Column(insertable = false ,updatable = false)
    private int availableStock;

    @Override
    public String toString() {
        return "ProductStock{" +
                "stock=" + stock +
                ", reservedStock=" + reservedStock +
                ", availableStock=" + availableStock +
                '}';
    }
}
