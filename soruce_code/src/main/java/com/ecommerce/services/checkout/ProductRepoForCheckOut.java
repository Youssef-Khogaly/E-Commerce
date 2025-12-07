package com.ecommerce.services.checkout;

import com.ecommerce.entities.Products.Product;

import java.util.Collection;
import java.util.List;

public interface ProductRepoForCheckOut {

    List<Product> findAllByIdForCheckout(Collection<Long> ids);
}
