package com.ecommerce.Product.repos;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.dtos.ProductSearchView;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.sql.Timestamp;
import java.util.Set;


@SqlResultSetMapping(
        name = "productSearchResult"
        , classes = @ConstructorResult(
        targetClass = ProductSearchView.class,
        columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "title", type = String.class),
                @ColumnResult(name = "price", type = Long.class),
                @ColumnResult(name = "imgUrl" , type = String.class),
                @ColumnResult(name = "date", type = Timestamp.class)

        }
    )
)
public interface ProductSearchNative extends Repository<Product, Long> {


    public enum ProductNativeSortOptions {
        PRICE("p.price"),
        DATE("p.added_at"),
        ASC("ASC"),
        DESC("Desc");

        private String sql;

        ProductNativeSortOptions(String sql) {
            this.sql = sql;
        }
        public String toSql(){
            return sql;
        }

    }



    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,p.price as price , i.image_url as imgUrl,p.addedAt as date from product p
                                                left join ProductImages pi on p.product_id = pi.product_id and pi.isMain = true
                                                left join image i on pi.image_id = i.image_id                                                                                                                       
                                                where  p.price between :minPrice and :maxPrice
                                                order by :sort          
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p 
                                                where p.price between :minPrice and :maxPrice
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProducts(Long minPrice , Long maxPrice , Pageable pageable, String sort);


    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,p.price as price , i.image_url as imgUrl,p.addedAt as date from product p
                                                left join ProductImages pi on p.product_id = pi.product_id and pi.isMain = true
                                                left join image i on pi.image_id = i.image_id                                                                                                                       
                                                where  p.price between :minPrice and :maxPrice
                                                             and exists(select 1 from product_category pc where pc.category_id in :categoriesIds and pc.product_id = p.product_id)
                                                order by :sort          
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p 
                                                where p.price between :minPrice and :maxPrice
                                                and  exists(select 1 from product_category pc where pc.category_id in :categoriesIds and pc.product_id = p.product_id)
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProducts(Set<Integer> categoriesIds , Long minPrice , Long maxPrice , Pageable pageable, String sort);
    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,p.price as price , i.image_url as imgUrl,p.addedAt as date from product p
                                                left join ProductImages pi on p.product_id = pi.product_id and pi.isMain = true
                                                left join image i on pi.image_id = i.image_id                                                                                                                       
                                                where MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE ) 
                                                                        and p.price between :minPrice and :maxPrice
                                                                        and exists(select 1 from product_category pc where pc.category_id in :categoriesIds and pc.product_id = p.product_id)
                                                order by :sort          
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p 
                                                where p.price between :minPrice and :maxPrice
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                                                and exists(select 1 from product_category pc where pc.category_id in :categoriesIds and pc.product_id = p.product_id)
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProducts(String searchText , Set<Integer> categoriesIds , Long minPrice , Long maxPrice , Pageable pageable, String sort);

    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,p.price as price,i.image_url as imgUrl ,p.addedAt as date from product p 
                                                        left join ProductImages pi on p.product_id = pi.product_id and pi.isMain = true
                                                        left join image i on pi.image_id = i.image_id                                                                                                      
                                                        where p.price between :minPrice and :maxPrice 
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                                                order by :sort
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p where p.price between :minPrice and :maxPrice
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProducts(String searchText, Long minPrice , Long maxPrice , Pageable pageable, String sort);

}