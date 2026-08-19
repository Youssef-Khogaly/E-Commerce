package com.ecommerce.Payment.repos;

import com.ecommerce.Payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepo extends JpaRepository<Payment, Long> {
    

    @Query("select p from Payment p where p.order.id = :orderId")
    Optional<Payment> findByOrderId(UUID orderId);


    boolean existsByTransactionId(String transactionId);
}
