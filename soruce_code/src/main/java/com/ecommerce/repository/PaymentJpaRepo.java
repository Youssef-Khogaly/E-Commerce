package com.ecommerce.repository;

import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepo extends JpaRepository<Payment, UUID> {

    @Transactional
    @Modifying
    @Query("update Payment set session_id = :sessionId where id = :id")
    void updateSessionId(UUID id , String sessionId);

    @Modifying
    @Query("update Payment set paymentState = :state where id = :id")
    void updatePaymentState(UUID id , PaymentState state);

    @Query("select p from Payment p where p.order.id = :orderId")
    Optional<Payment> findByOrderId(UUID orderId);
}
