package com.ecommerce.entities.Products;

import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.images.Image;
import com.ecommerce.entities.review.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Instant addedAt;
    @OneToOne(mappedBy = "product" , fetch = FetchType.EAGER , optional = false , cascade = CascadeType.PERSIST)
    private ProductStock stock;

    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY )
    private Set<Image> images;

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
