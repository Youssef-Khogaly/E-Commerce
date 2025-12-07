package com.ecommerce.repository.Payments;

import com.ecommerce.entities.Payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentsJpaRepo extends JpaRepository<Payment, UUID> {
}
