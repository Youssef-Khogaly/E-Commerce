package com.ecommerce.inventory.service;

import java.util.Collection;
import java.util.Set;

public interface StockPoolRefillService {

    Integer MAX_REFILL_QUANTITY = 500;
    /*
       if pooled quantity less than500
       we trigger refill
     */
    Integer REFILL_THRESHOLD = 200;
    void refill(Long productId );

}
