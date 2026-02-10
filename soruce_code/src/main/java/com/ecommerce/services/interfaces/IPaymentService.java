package com.ecommerce.services.interfaces;

import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPaymentService {


    Payment  create(Order order , PaymentMethod method);
    Payment save(Payment payment);
    void updateState(UUID id , PaymentState state);
    void updateState(Payment payment, PaymentState state);
    Optional<Payment> findPayment(UUID id);
    void deletePayment(UUID id);
    Optional<Payment> findByOrderId(UUID orderId);
}
