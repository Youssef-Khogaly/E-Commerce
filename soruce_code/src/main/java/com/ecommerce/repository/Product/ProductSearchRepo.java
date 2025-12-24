package com.ecommerce.repository.Product;

import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductSearchRepo  implements IProductSearchRepo {

    @PersistenceContext
    private EntityManager em;


    private Long countTotalCiteriaQuery(Integer categoryId ,  long minPrice
            , long maxPrice)
    {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Product> productRoot = query.from(Product.class); // select p from product p
        Boolean containDuplicates = false;
        query.where(builder.and(buildPredicates(categoryId,minPrice,maxPrice,productRoot,builder)));
        if(categoryId  != null) // duplicates
        {
            query.select(builder.countDistinct(productRoot.get("id")));
        }
        else
            query.select(builder.count(productRoot.get("id")));

        TypedQuery<Long> typedQuery = em.createQuery(query);

        return typedQuery.getSingleResult();
    }
    private  Predicate[] buildPredicates( Integer categoryId , @PositiveOrZero long minPrice
            , @Positive long maxPrice , Root<Product> productRoot , CriteriaBuilder builder )
    {
        List<Predicate> predicateList = new ArrayList<>(10);
        if(categoryId != null)
        {
            Join<Product, Category> categoryJoin = productRoot.join("categories", JoinType.INNER);
            predicateList.add(builder.equal(categoryJoin.get("cate_id"),categoryId));
        }
        // price filter
        predicateList.add(builder.between(productRoot.get("price") , minPrice , maxPrice));

        return predicateList.toArray(new Predicate[0]);
    }
    // no native query required
    private  Page<Product> searchUsingCiteria(@Nullable Integer categoryId , @PositiveOrZero long minPrice
            , @Positive long maxPrice, Pageable pageable)
    {
        long totalCount = -1;
        List<Product> productList;
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = builder.createQuery(Product.class);
        Root<Product> productRoot = query.from(Product.class); // select p from product p
        productRoot.fetch("stock",JoinType.INNER);
        query.select(productRoot);
        Sort sort = pageable.getSort();
        Predicate[] predicates = buildPredicates(categoryId,minPrice,maxPrice,productRoot,builder);
        query.where(builder.and(predicates));

        CriteriaQuery<Product> finalQuery = query;
        List<Order> orderList = new ArrayList<>(5);
        sort.get().forEach(
                o ->{
                    if(o.isAscending())
                        orderList.add(builder.asc(productRoot.get(o.getProperty())));
                    else
                        orderList.add(builder.desc(productRoot.get(o.getProperty())));
                }
        );
        query.orderBy(orderList);
        TypedQuery<Product> typedQuery = em.createQuery(query);

        typedQuery.setFirstResult((pageable.getPageNumber()) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

        /*check if count calculated before send search query
        *  if count = 0 no need to send query it will return no result anyway
        *
        * */
        totalCount = countTotalCiteriaQuery(categoryId,minPrice,maxPrice);
        if(totalCount > 0)
         productList = typedQuery.getResultList();
        else
            productList = new ArrayList<>(0);
        return new PageImpl<>(productList,pageable,totalCount);
    }
    public Page<Product> searchForProducts(@Nullable String name , @Nullable Integer categoryId , @PositiveOrZero long minPrice
            , @Positive long maxPrice, Pageable pageable)
    {
        // no need to use native query
        if(name == null){
            return searchUsingCiteria(categoryId,minPrice,maxPrice,pageable);
        }
        // we have to use native query to use full text mysql index


        return  null;
    }

}
