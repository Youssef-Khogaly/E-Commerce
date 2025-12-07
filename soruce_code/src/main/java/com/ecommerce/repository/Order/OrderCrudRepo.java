package com.ecommerce.repository.Order;


import com.ecommerce.entities.orders.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface OrderCrudRepo extends CrudRepository<Order, UUID> {


    @EntityGraph(
            attributePaths = {"orderItems","payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id = :id ")
    public Order findByIdForPaymentEvent(UUID id);


    @EntityGraph(
            attributePaths = {"payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.customer.id = :cust_id")
    public List<Order> findAllByCustomerIdListView(Long cust_id);

    @EntityGraph(
            attributePaths = {"payment","orderItems"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id =:orderId and o.customer.id = :cust_id")
    public Order findByCustomerIdAndOrderId(UUID orderId , Long cust_id);

    @EntityGraph(
            attributePaths = {"orderItems","payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id = :id and o.customer.id = :cust_id")
    public Order findWithAllByIdAndCustId(UUID id, Long cust_id);
}
