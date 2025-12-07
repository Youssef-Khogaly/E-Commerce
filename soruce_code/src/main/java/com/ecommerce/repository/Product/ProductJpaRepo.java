package com.ecommerce.repository.Product;

import com.ecommerce.DTO.productCategoryRow;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;

import com.ecommerce.services.checkout.ProductRepoForCheckOut;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductJpaRepo extends JpaRepository<Product, Long> , ProductQueryRepo , ProductRepoForCheckOut {

    @Query(
            """
            select case 
            when exists (select 1 from Product p where  p.id=:id  )
                        then true
                        else false end
            """
    )
    boolean isExists(Long id);

    @EntityGraph(
            attributePaths = {"stock","images","categories"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Override
    Optional<Product> findById(Long aLong);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(
            attributePaths = {"stock"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);

    @Query("select  p.categories from Product p")
    Set<Category> findCategoriesById(Long product_id);

    @Query("select new com.ecommerce.DTO.productCategoryRow(p.id , c.cate_id , c.name)  from Product p  inner join  Category c on p.id = c.cate_id ")
    List<productCategoryRow>findAllCategoriesById(Collection<Long>ids);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(
            attributePaths = {"images","stock"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select p from Product p  where  p.id in :ids")
    List<Product> findAllByIdForCheckout(Collection<Long> ids);





    @EntityGraph(
            attributePaths = {"images","categories","stock"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query(
            """
        select p from Product p
                where p.id in :ids
        """
    )
    List<Product>findAllByIdReadOnly(Collection<Long>ids);
}
