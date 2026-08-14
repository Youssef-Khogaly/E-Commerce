package com.ecommerce.DTO;

import com.ecommerce.entities.Products.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO{
    @Schema(name = "Product id", example = "1")
    @Positive
    long id;
    @Schema(example = "laptop")
    @NotNull
    @NotBlank
    String name ;
    @Nullable
    String description ;
    @NotNull
    Money price;
    @Nullable
    DiscountDTO discount;
    @Nullable
    List<ImageDTO> imagesUrl;


    public static ProductDTO fromProduct(Product product){
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPriceMoney(),new DiscountDTO(0)
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
