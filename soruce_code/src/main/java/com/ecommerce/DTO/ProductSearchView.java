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
    private final long priceInCents;
    private final long discountInCents;
    private final Instant addedAt;

    public ProductSearchView(long id, String title, int availableStock, long priceInCents , Instant addedAt ) {
        this.id = id;
        this.title = title;
        this.availableStock = availableStock;
        this.priceInCents = priceInCents;
        this.addedAt = addedAt;
        this.discountInCents = 0;
    }

}
