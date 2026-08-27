package com.ecommerce.Product.repos;

import com.ecommerce.Category.entity.Category;

import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.dtos.productCategoryRow;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface ProductJpaRepo extends JpaRepository<Product, Long> , ProductQueryRepo  {

    @Query(
            """
            select case 
            when exists (select 1 from Product p where  p.id=:id  )
                        then true
                        else false end
            """
    )
    boolean isExists(Long id);

    @Override
    Optional<Product> findById(Long id);

    @Query("""
            select new  com.ecommerce.Product.dtos.ProductSearchView(p.id , p.title  , p.price  , i.imageUrl , p.addedAt)  from Product p
            left join ProductImages  pi on p.id=pi.productImageId.product_id and pi.isMain = true  left join Image i  on pi.image.id = i.id  
            where p.id in :ids 
                        """ )
    List<ProductSearchView>findAllByidsForProductSearchView(Collection<Long> ids);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(
            attributePaths = {},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);


    @Query(
            """
            select p.id from Product p where p.id in :ids
            """
    )
    Set<Long> getExistingIds(Set<Long> ids);




    @Query(
            """
        select p from Product p
                where p.id in :ids
        """
    )
    List<Product>findAllByIdReadOnly(Collection<Long>ids);

    @Query("select p from Product p where p.id = :product_id")
    Optional<Product> findByIdWithImagesOnly(long product_id);

    Product id(long id);
}
