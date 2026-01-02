package com.ecommerce.DTO;

import com.ecommerce.entities.images.Image;
import lombok.Getter;

import java.sql.Time;
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
    private final long priceInCents;
    private final long discountInCents;
    private final String mainImgUrl;
    private final Instant addedAt;

    public ProductSearchView(Long id, String title, Integer availableStock, Long priceInCents, String mainImgUrl, Timestamp addedAt ) {
        this.id = id;
        this.title = title;
        this.availableStock = availableStock;
        this.priceInCents = priceInCents;
        this.mainImgUrl = mainImgUrl;
        this.addedAt = addedAt.toInstant();
        this.discountInCents = 0;
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
