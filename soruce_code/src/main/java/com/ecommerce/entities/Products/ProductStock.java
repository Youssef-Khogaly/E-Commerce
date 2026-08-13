package com.ecommerce.entities.Products;

import com.ecommerce.services.StockService.OutOfStock;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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
@Getter
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


    public void setProduct_id(long product_id) {
        this.product_id = product_id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    private void setStock(int stock) {
        this.stock = stock;
    }

    private void setReservedStock(int reservedStock) {
        this.reservedStock = reservedStock;
    }

    public void add(int quantity)
    {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;
        setStock(getStock()+quantity);
    }
    public void remove( int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;
        if(availableStock < quantity)
            throw  new OutOfStock("can't remove from stock , available stock will be negative!! , pid: " + getProduct_id());
        setStock(getStock()-quantity);

    }


    public void reserve(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;
        if(getAvailableStock() < quantity)
            throw new OutOfStock("not enough stock for product:"+getProduct_id() + "available stock:" + getAvailableStock());

        setReservedStock(getReservedStock()+quantity);

    }

    public void release(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;

        if(quantity > getReservedStock())
            throw  new OutOfStock("can't release stock , reserved will be negative!! , pid: " + getProduct_id() + " quantity to release:" +quantity);

        setReservedStock(getReservedStock()-quantity);

    }


    public void commit(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;

        if(quantity > getReservedStock())
            throw  new OutOfStock("can't commit stock , reserved will be negative!! , pid: " + getProduct_id() + "quantity to commit:" +quantity);
        if(quantity > getStock())
            throw  new OutOfStock("can't commit stock , stock will be negative!! , pid: " + getProduct_id() + "quantity to commit:" +quantity);

        setReservedStock(getReservedStock()-quantity);
        setStock(getStock()-quantity);
    }











    @Override
    public String toString() {
        return "ProductStock{" +
                "stock=" + stock +
                ", reservedStock=" + reservedStock +
                ", availableStock=" + availableStock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductStock that)) return false;
        return getProduct_id() == that.getProduct_id();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getProduct_id());
    }
}
