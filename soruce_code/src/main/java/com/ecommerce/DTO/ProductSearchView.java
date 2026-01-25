package com.ecommerce.DTO;

import lombok.Getter;

import java.sql.Timestamp;
import java.time.Instant;


/*

    DTO for product searching
 */
@Getter
public class ProductSearchView {
    private final long id;
    private final String title;
    private final int availableStock;
    private final Money priceInCents;
    private final DiscountDTO discount;
    private final String mainImgUrl;
    private final Instant addedAt;

    public ProductSearchView(Long id, String title, Integer availableStock, Long priceInCents, String mainImgUrl, Timestamp addedAt ) {
        this.id = id;
        this.title = title;
        this.availableStock = availableStock;
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

}
