package com.ecommerce.Product.dtos;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Images.dtos.ImageDTO;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.util.Money;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Nullable
    List<Category> categories;

    public static ProductDTO fromProduct(Product product,List<ImageDTO>images,List<Category> categories){
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPriceMoney(),new DiscountDTO(0),images,categories);
    }
    public static ProductDTO fromProduct(Product product,List<ImageDTO>images){
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPriceMoney(),new DiscountDTO(0),images,null);
    }
    public static ProductDTO fromProduct(Product product){
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPriceMoney(),new DiscountDTO(0),null,null);
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
