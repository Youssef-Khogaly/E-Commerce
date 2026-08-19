package com.ecommerce.Category.entity;

import com.ecommerce.Product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor
public class ProductCategory {


    @EmbeddedId
    private ProductCategoryId productCategoryId;

    @MapsId("productId")
    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    public ProductCategory(Long productId, Integer categoryId) {
        this.productCategoryId = new ProductCategoryId(productId,categoryId);
    }



    public Long getProductId()
    {
        return getProductCategoryId().getProductId();
    }
    public Integer getCategoryId()
    {
        return getProductCategoryId().getCategoryId();
    }
}
