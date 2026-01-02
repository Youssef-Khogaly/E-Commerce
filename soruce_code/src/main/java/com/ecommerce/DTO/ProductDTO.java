package com.ecommerce.DTO;

import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.Products.ProductImages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO{
    long id;
    String name ;
    String description ;
    long priceInCents;
    long discountInCents = 0;
    int availableStock ;
    List<ImageDTO> imagesUrl;


    public static ProductDTO fromProduct(Product product){
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPrice(),0,product.getStock().getAvailableStock()
                , product.getImagesList().stream().map(pi -> ImageDTO.fromImage(pi.getImage())).toList());
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO that = (ProductDTO) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
