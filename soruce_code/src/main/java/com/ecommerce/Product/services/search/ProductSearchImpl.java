package com.ecommerce.Product.services.search;

import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.ProductSortByOptions;
import com.ecommerce.Product.entity.ProductSortDirection;
import com.ecommerce.Product.repos.ProductSearchNative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ProductSearchImpl implements ProductSearchService{
    private final ProductSearchNative productSearchRepo;
    public ProductSearchImpl(ProductSearchNative productSearchRepo) {
        this.productSearchRepo = productSearchRepo;
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
    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct) {
        int pageNum = queryProduct.page();
        int pageSize = queryProduct.pageSize();
        Sort.Direction direction = (queryProduct.direction() == ProductSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortby = queryProduct.sortBy().toProductField();
        Sort sort = Sort.by(direction,sortby);

        Pageable page = PageRequest.of(pageNum,pageSize,sort);
        String searchQuery = normalizeSearchQuery(queryProduct.name());
        Set<Integer> catIds = queryProduct.categoryids();
        String sortString = buildSort(page.getSort());

        if(catIds == null || catIds.isEmpty())
        {
            if(searchQuery != null && !searchQuery.isBlank())
                return productSearchRepo.searchForProducts(searchQuery,queryProduct.minPrice(),queryProduct.maxPrice(),page,sortString);
            else
                return productSearchRepo.searchForProducts(queryProduct.minPrice(),queryProduct.maxPrice(),page,sortString);
        }

        if(searchQuery != null && !searchQuery.isBlank())
            return productSearchRepo.searchForProducts(catIds ,queryProduct.minPrice(),queryProduct.maxPrice(),page,sortString);

        return productSearchRepo.searchForProducts(searchQuery, catIds ,queryProduct.minPrice(),queryProduct.maxPrice(),page,sortString);
    }


    private String buildSort(Sort sort)
    {
        StringBuilder sortBuilder = new StringBuilder(10);
        sort.get().forEach(
                o ->{
                    if(o.getProperty().equalsIgnoreCase(ProductSortByOptions.PRICE.toProductField()))
                    {
                        sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.PRICE.toSql()).append(' ');
                        if(o.isAscending()){
                            sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.ASC.toSql()).append(',');
                        }
                        else if (o.isDescending())
                        {
                            sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.DESC.toSql()).append(',');
                        }
                    } else if (o.getProperty().equalsIgnoreCase(ProductSortByOptions.DATE.toProductField())) {
                        sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.DATE.toSql()).append(' ');
                        if(o.isAscending()){
                            sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.ASC.toSql()).append(',');
                        }
                        else if (o.isDescending())
                        {
                            sortBuilder.append(ProductSearchNative.ProductNativeSortOptions.DESC.toSql()).append(',');
                        }
                    }

                }
        );
        sortBuilder.deleteCharAt(sortBuilder.length()-1);

        return sortBuilder.toString();
    }
}
