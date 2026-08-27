package com.ecommerce.Product.entity;

import com.ecommerce.util.Money;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import java.sql.Timestamp;
import java.util.*;

@DynamicUpdate
@Entity
@Table(name = "product")
@Getter@Setter
public class Product{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long id;
    @Length(min = 6 , max = 63 , message = "title must be between 6 and 63")
    private String title;
    private String description;
    private long price;

    private Timestamp addedAt;


    @JsonIgnore
    public Money getPriceMoney(){
        return new Money(getPrice());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
