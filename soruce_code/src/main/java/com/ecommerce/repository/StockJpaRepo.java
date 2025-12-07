package com.ecommerce.repository;

import com.ecommerce.entities.Products.ProductStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockJpaRepo extends JpaRepository<ProductStock,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ProductStock s where s.product.id in :ids")
    List<ProductStock> findAllByIdForUpdate(List<Long> ids);



}
