package com.ecommerce.repository;

import com.ecommerce.entities.Payments.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepo extends JpaRepository<Payment, UUID> {
}
