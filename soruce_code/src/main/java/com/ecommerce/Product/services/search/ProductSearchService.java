package com.ecommerce.Product.services.search;

import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.ProductSortByOptions;
import com.ecommerce.Product.entity.ProductSortDirection;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Map;

public interface ProductSearchService {

    public static record QueryProduct(int page, int pageSize
            , String name , Long minPrice , Long maxPrice
            , Integer category
            , ProductSortByOptions sortBy, ProductSortDirection direction
    ){}

    public Map<Long,ProductSearchView> getProductSearchView(Collection<Long> ids);
    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct);
}
