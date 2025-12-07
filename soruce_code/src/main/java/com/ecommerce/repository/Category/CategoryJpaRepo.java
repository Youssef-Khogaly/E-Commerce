package com.ecommerce.repository.Category;

import com.ecommerce.entities.Categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface CategoryJpaRepo extends JpaRepository<Category,Integer> {

    @Query("""
    select case when 
    count(c.cate_id) = :size then true
    else false
    end 
    from Category c
    where c.cate_id  in :ids

""")
    boolean isAllExistsByIds(Set<Integer> ids, Long size);

    @Query("""

            select c.cate_id from Category c
        """)
    Set<Integer >getAllIds();

    Category findByName(String name);

    boolean existsByName(String name);
}