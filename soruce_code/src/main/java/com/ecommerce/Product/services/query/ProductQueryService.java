package com.ecommerce.Product.services.query;

import java.util.Map;
import java.util.Set;

public interface ProductQueryService<T> {

    T findById(Long productId);
    Map<Long,T> findAllByIds(Set<Long> ids);

}
