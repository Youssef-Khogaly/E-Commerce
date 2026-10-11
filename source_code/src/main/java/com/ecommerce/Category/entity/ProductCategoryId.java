package com.ecommerce.Category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductCategoryId implements Serializable {

    @Column(name = "product_id",nullable = false)
    private Long productId;
    @Column(name = "category_id",nullable = false)
    private Integer categoryId;
}
