package com.ecommerce.repository.Product;

import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.entities.Products.Product;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.sql.Timestamp;
import java.time.Instant;


@SqlResultSetMapping(
        name = "productSearchResult"
        , classes = @ConstructorResult(
        targetClass = ProductSearchView.class,
        columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "title", type = String.class),
                @ColumnResult(name = "stock", type = Integer.class),
                @ColumnResult(name = "price", type = Long.class),
                @ColumnResult(name = "date", type = Instant.class)

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
        String toSql(){
            return sql;
        }

    }

    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,s.availableStock as stock,p.price as price , p.addedAt as date from product p inner join product_category c on p.product_id = c.product_id
                                                and c.category_id = :categoryId inner join product_stock s on p.product_id = s.product_id
                                                where p.price between :minPrice and :maxPrice
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                                                order by :sort
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p inner join product_category c on p.product_id = c.product_id
                                                and c.category_id = :categoryId
                                                where p.price between :minPrice and :maxPrice
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProductsWithOrder(String searchText , Integer categoryId , Long minPrice , Long maxPrice , Pageable pageable, String sort);

    @Query
            (
                    value = """

                            select p.product_id as id, p.title as title,s.availableStock as stock,p.price as price, p.addedAt as date from product p inner join product_stock s on p.product_id = s.product_id     where p.price between :minPrice and :maxPrice 
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                                                order by :sort
                        """
                    ,
                    countQuery = """
                        select count(p.product_id) from product p where p.price between :minPrice and :maxPrice
                                                and  MATCH(p.title) AGAINST (:searchText IN BOOLEAN MODE )
                        """ ,nativeQuery = true
            )

    Page<ProductSearchView> searchForProductsOrderByWithoutCategroy(String searchText,Long minPrice , Long maxPrice , Pageable pageable, String sort);

}