package com.ecommerce.repository;

import com.ecommerce.entities.Products.ProductStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StockJpaRepo extends JpaRepository<ProductStock,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProductStock s where s.id in :ids")
    Set<ProductStock> findAllByIdForUpdate(Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProductStock s where s.id = :id")
    Optional<ProductStock> findByIdForUpdate(Long id);

    @Query("select s from ProductStock s where s.id in :ids")
    Set<ProductStock> findAllByIdReadOnly(Collection<Long> ids);

}
