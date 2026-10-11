package com.ecommerce.Payment.services;

import com.ecommerce.Payment.entity.Payment;
import com.ecommerce.Payment.entity.PaymentMethod;
import com.ecommerce.Payment.entity.PaymentState;
import com.ecommerce.orders.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface IPaymentService {


    Payment create(Order order , PaymentMethod method);
    Payment save(Payment payment);
    void updateState(UUID id , PaymentState state);
    void updateState(Payment payment, PaymentState state);
    Optional<Payment> findPayment(UUID id);
    void deletePayment(UUID id);
    Optional<Payment> findByOrderId(UUID orderId);
}
