package com.ecommerce.entities.Products;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageId implements Serializable {
    private Long product_id;
    private Long image_id;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductImageId that = (ProductImageId) o;
        return product_id == that.product_id && image_id == that.image_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product_id, image_id);
    }
}
