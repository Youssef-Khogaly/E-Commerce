package com.ecommerce.entities.review;

import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.service.annotation.GetExchange;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "review")
@IdClass(ReviewID.class)
public class Review {

    @Getter
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="cust_id")
    private Customer customer;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @PositiveOrZero
    @Range(min = 0 , max = 5 , message = "Rating must be between 0 and 5")
    private int rating;

    private Timestamp updated_at; // database triggers generate it
    private Timestamp created_at; // database trigger generate it
    private String comment;


    @PrePersist
    public void OnCreate(){
        created_at = new Timestamp(System.currentTimeMillis());
    }
    @PreUpdate
    public void OnUpdate(){
        updated_at = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(getCustomer(), review.getCustomer()) && Objects.equals(getProduct(), review.getProduct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomer(), getProduct());
    }
}
