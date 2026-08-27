package com.ecommerce.Product.services.search;

import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.ProductSortDirection;
import com.ecommerce.Product.repos.IProductSearchRepo;
import com.ecommerce.Product.repos.ProductJpaRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductSearchImpl implements ProductSearchService{
    private final IProductSearchRepo productSearchRepo;
    private final ProductJpaRepo productJpaRepo;
    public ProductSearchImpl(IProductSearchRepo productSearchRepo, ProductJpaRepo productJpaRepo) {
        this.productSearchRepo = productSearchRepo;
        this.productJpaRepo = productJpaRepo;
    }

    private String normalizeSearchQuery(String name){
        if(name == null || name.isBlank())
            return null;
        // build search query match
        // allow only  chars and numbers and space
        String normalizedTxt = name.trim().toLowerCase().replaceAll("[^a-z0-9\\s]" , "");
        if(normalizedTxt.isBlank())
            throw new BadRequestException("only English chars and number are allowed in search query");
        String[] words = normalizedTxt.split("\\s+");
        StringBuilder searchQuery = new StringBuilder(name.length());

        for(String str : words)
        {
            if(!str.isBlank()){
                searchQuery.append('+').append(str).append(' ');
            }
        }
        return searchQuery.toString();
    }


    @Override
    public Map<Long, ProductSearchView> getProductSearchView(Collection<Long> ids) {
        return Map.of();
    }

    @Override
    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct) {
        int pageNum = queryProduct.page();
        int pageSize = queryProduct.pageSize();
        Sort.Direction direction = (queryProduct.direction() == ProductSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortby = queryProduct.sortBy().toProductField();
        Sort sort = Sort.by(direction,sortby);

        Pageable page = PageRequest.of(pageNum,pageSize,sort);
        String searchQuery = normalizeSearchQuery(queryProduct.name());
        Integer catId = queryProduct.category();
        return productSearchRepo.searchForProducts(searchQuery, catId ,queryProduct.minPrice(),queryProduct.maxPrice(),page);
    }
}
