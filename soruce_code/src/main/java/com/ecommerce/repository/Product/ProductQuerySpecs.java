package com.ecommerce.repository.Product;

import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.user.Customer;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

public class ProductQuerySpecs {

    public static enum SearchEnum{
        TextAsSubStringInTitle
    }
    public static Specification<Product> empty() {
        return (root, query, criteriaBuilder) -> null;
    }
    // return empty of null
    public static Specification<Product> priceLessThanOrEqual(Long maxVal)
    {
        if(maxVal == null)
            return ProductQuerySpecs.empty();
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"),maxVal);
        });
    }

    static public Specification<Product>priceGreaterThanOrEqual(Long minVal){

        if(minVal == null)
            return ProductQuerySpecs.empty();

        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"),minVal);
        });
    }
    static public Specification<Product>searchInTitle(String text){

        if(text == null || text.isBlank()) {
            return empty();
        }
        String normalizedTxt = text.trim().toLowerCase(Locale.ROOT).replaceAll("//s{2,}"," ");

        String regex = "%" + normalizedTxt +"%";

        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(root.get("title"),regex);
        });
    }
    static public Specification<Product>hasCategory(Integer categoriesId){

        if(categoriesId == null){
            return empty();
        }
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.equal(root.join("categories",JoinType.INNER).get("cate_id"),categoriesId);
        };
    }


}
