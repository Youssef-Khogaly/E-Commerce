package com.ecommerce.repository.Product;

import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.DTO.productCategoryRow;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;

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

    @EntityGraph(
            attributePaths = {"stock","imagesList","imagesList.image"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Override
    Optional<Product> findById(Long id);

    @Query("""
            select new  com.ecommerce.DTO.ProductSearchView(p.id , p.title , s.availableStock , p.price  , i.imageUrl , p.addedAt)  from Product p inner join ProductStock s on p.id= s.product_id 
            left join ProductImages  pi on p.id=pi.productImageId.product_id and pi.isMain = true  left join Image i  on pi.image.id = i.id 
            where p.id in :ids 
                        """ )
    List<ProductSearchView>findAllByidsForProductSearchView(Collection<Long> ids);
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






    @EntityGraph(
            attributePaths = {"imagesList","categories","stock"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query(
            """
        select p from Product p
                where p.id in :ids
        """
    )
    List<Product>findAllByIdReadOnly(Collection<Long>ids);

    @EntityGraph(
            attributePaths = {"imagesList","imagesList.image"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select p from Product p where p.id = :product_id")
    Optional<Product> findByIdWithImagesOnly(long product_id);

}
