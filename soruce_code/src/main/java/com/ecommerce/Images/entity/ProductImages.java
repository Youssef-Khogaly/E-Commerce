package com.ecommerce.Images.entity;

import com.ecommerce.Product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProductImages {
    @EmbeddedId
    private ProductImageId productImageId;

    @MapsId("product_id")
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
    @MapsId("image_id")
    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id",nullable = false)
    private Image image;

    private boolean isMain;

    public ProductImages(Long productId,Long imageId) {
        this.productImageId = new ProductImageId(productId,imageId);
    }

    public Long getProductId()
    {
        return getProductImageId().getProduct_id();
    }
    public Long getImageId()
    {
        return getProductImageId().getImage_id();
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductImages that = (ProductImages) o;
        return Objects.equals(getProductImageId(), that.getProductImageId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getProductImageId());
    }
}
