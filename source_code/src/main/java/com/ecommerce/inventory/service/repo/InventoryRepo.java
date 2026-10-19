package com.ecommerce.inventory.service.repo;

import com.ecommerce.inventory.infra.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface InventoryRepo extends JpaRepository<Inventory,Long> {


    @Query(value = """
            select * from inventory where id in :ids for update no wait
            """

            ,nativeQuery = true)
    public List<Inventory> findAllByIdForUpdateNoWait(@Param("ids") Collection<Long> ids);
    @Query(value = """
            select * from inventory where id = :id for update no wait
            """

            ,nativeQuery = true)
    public Inventory findByIdForUpdateNoWait(@Param("id")Long id);

    @Query(value = """
            select * from inventory where id in :ids for update
            """

            ,nativeQuery = true)

    public List<Inventory>findAllByIdForUpdate(@Param("ids")Collection<Long>ids);
    @Query(value = """
            select * from inventory where id = :id for share 
            """

            ,nativeQuery = true)
    public Inventory findByIdForShare(Long id);

    @Query(value = "select p.id from Inventory p where p.id in :ids")
    public List<Long> getExists(@Param("ids")Collection<Long> ids);
}
