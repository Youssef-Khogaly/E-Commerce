package com.ecommerce.repository;

import com.ecommerce.entities.images.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface ImagesJpaRepo extends JpaRepository<Image,Long> {

    @Query("select i.id from Image i where i.id in :ids")
    Set<Long> findExistingIds(Collection<Long>ids);
}
