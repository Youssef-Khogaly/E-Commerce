package com.ecommerce.repository.Order;


import com.ecommerce.entities.orders.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepo extends JpaRepository<Order, Long> {


    @EntityGraph(
            attributePaths = {"orderItems","payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id = :id ")
    public Order findByIdForPaymentEvent(Long id);


    @EntityGraph(
            attributePaths = {"payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.customer_id = :cust_id")
    public List<Order> findAllByCustomerIdListView(Long cust_id);

    @EntityGraph(
            attributePaths = {"payment","orderItems"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id =:orderId and o.customer_id = :cust_id")
    public Order findByCustomerIdAndOrderId(Long orderId , Long cust_id);

    @EntityGraph(
            attributePaths = {"orderItems","payment"},
            type = EntityGraph.EntityGraphType.FETCH
    )
    @Query("select o from Order o where o.id = :id and o.customer_id = :cust_id")
    public Order findWithAllByIdAndCustId(Long id, Long cust_id);
}
