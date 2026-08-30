package com.ecommerce.Product.dtos;

import com.ecommerce.util.Money;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;


/*

    DTO for product searching
 */
@Getter
public class ProductSearchView {

    @Schema(name = "Product id", example = "1")
    @Positive
    private final long id;
    @Schema(example = "laptop")
    @NotBlank
    private final String title;
    @NotNull
    private final Money priceInCents;
    @Nullable
    private final DiscountDTO discount;
    @Schema(example = "wwww.s3.amazon.com/image3")
    @Nullable
    private final String mainImgUrl;
    @NotNull
    private final Instant addedAt;

    public ProductSearchView(Long id, String title, Long priceInCents, String mainImgUrl, Timestamp addedAt ) {
        this.id = id;
        this.title = title;
        this.priceInCents = new Money(priceInCents);
        this.mainImgUrl = mainImgUrl;
        this.addedAt = addedAt.toInstant();
        this.discount = new DiscountDTO(0);
    }
//    public ProductSearchView(Long id, String title, Integer availableStock, Long priceInCents, String mainImgUrl, Instant addedAt ) {
//        this.id = id;
//        this.title = title;
//        this.availableStock = availableStock;
//        this.priceInCents = priceInCents;
//        this.mainImgUrl = mainImgUrl;
//        this.addedAt = addedAt;
//        this.discountInCents = 0;
//    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProductSearchView that)) return false;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
