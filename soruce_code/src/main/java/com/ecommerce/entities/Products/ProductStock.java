package com.ecommerce.entities.Products;

import com.ecommerce.services.StockService.OutOfStock;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class ProductStock {

    @Id
    @Column(name = "product_id")
    private Long id;

    @PositiveOrZero
    private Integer stock;
    @PositiveOrZero
    private Integer reservedStock;
    @Column(insertable = false ,updatable = false)
    private Integer availableStock;


    public Long getId() {
        return id;
    }

    public Integer getStock() {
        return stock;
    }

    public Integer getReservedStock() {
        return reservedStock;
    }

    public Integer getAvailableStock() {
        return getStock() - getReservedStock();
    }

    public ProductStock(Long id, Integer stock) {
        if(stock < 0)
            throw new IllegalArgumentException("cannot create stock with negative quantity!!");
        setId(id);
        setStock(stock);
    }

    public void setId(long product_id) {
        this.id = product_id;
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
            throw  new OutOfStock("can't remove from stock , available stock will be negative!! , pid: " + getId());
        setStock(getStock()-quantity);

    }


    public void reserve(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;
        if(getAvailableStock() < quantity)
            throw new OutOfStock("not enough stock for product:"+ getId() + "available stock:" + getAvailableStock());

        setReservedStock(getReservedStock()+quantity);

    }

    public void release(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;

        if(quantity > getReservedStock())
            throw  new OutOfStock("can't release stock , reserved will be negative!! , pid: " + getId() + " quantity to release:" +quantity);

        setReservedStock(getReservedStock()-quantity);

    }


    public void commit(int quantity) {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot update stock with negative quantity!!");
        if(quantity == 0)
            return;

        if(quantity > getReservedStock())
            throw  new OutOfStock("can't commit stock , reserved will be negative!! , pid: " + getId() + "quantity to commit:" +quantity);
        if(quantity > getStock())
            throw  new OutOfStock("can't commit stock , stock will be negative!! , pid: " + getId() + "quantity to commit:" +quantity);

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
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
