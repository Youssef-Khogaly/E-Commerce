package com.ecommerce.Product.entity;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.util.Money;
import com.ecommerce.Category.entity.Category;
import com.ecommerce.Images.entity.ProductImages;
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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id" , nullable = false ),
            inverseJoinColumns = @JoinColumn(name = "category_id" , nullable = false)
    )
    private Set<Category> categories;
    @Column(updatable = false,insertable = false,nullable = false)
    private Timestamp addedAt;


    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ProductImages> imagesList = new ArrayList<>();


    public Money getMoneyPrice(){
        return new Money(price, ApplicationConstants.defaultCurrency);
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(getId(), product.getId());
    }
    public Money getPriceMoney(){
        return new Money(getPrice());
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
