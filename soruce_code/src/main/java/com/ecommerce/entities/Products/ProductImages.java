package com.ecommerce.entities.Products;

import com.ecommerce.entities.images.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class ProductImages {
    @EmbeddedId
    private ProductImageId productImageId;

    @MapsId("product_id")
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @MapsId("image_id")
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    private boolean isMain;

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
