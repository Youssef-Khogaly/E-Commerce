package com.ecommerce.services;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.repository.PaymentJpaRepo;
import com.ecommerce.services.Exceptions.IllegalPaymentState;
import com.ecommerce.services.interfaces.IPaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class PaymentService implements IPaymentService {

    private final PaymentJpaRepo paymentJpaRepo;
    @Override
    public Payment create(Order order, PaymentMethod method) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentState(PaymentState.PENDING);
        payment.setPaymentMethod(method);
        return paymentJpaRepo.save(payment);
    }


    @Override
    public Payment save(Payment payment) {

        return paymentJpaRepo.save(payment);
    }

    @Override
    @Transactional
    public void updateState(UUID id, PaymentState state) {
        Payment payment = paymentJpaRepo.findById(id).orElseThrow(() -> new NotFoundException("Payment does not exists!!, id:" + id));

        updateState(payment,state);

    }

    @Override
    public void updateState(Payment payment, PaymentState state) {
        if(payment.getPaymentState() == PaymentState.FAILED || payment.getPaymentState() == PaymentState.EXPIRED)
            throw new IllegalPaymentState("Can't update payment state from failed or expired");
        else if (payment.getPaymentState() == PaymentState.REFUNDED){
            throw new IllegalPaymentState("Can't update payment state already refunded");
        }
        else if(payment.getPaymentState() == PaymentState.PENDING){
            if(state == PaymentState.REFUNDED){
                throw new IllegalPaymentState("Can't update payment state from pending to refunded");
            }
            payment.setPaymentState(state);
        }
        else if (payment.getPaymentState() == PaymentState.CONFIRMED)
        {
            if(state == PaymentState.FAILED || state== PaymentState.EXPIRED)
                throw new IllegalPaymentState("Can't update fail or expired payment that already paid!!");
            if(state == PaymentState.PENDING)
                throw new IllegalPaymentState("Can't payment state can not be updated from confirmed to pending!!");

            payment.setPaymentState(state);
        }
    }

    @Override
    public Optional<Payment> findPayment(UUID id) {
        return paymentJpaRepo.findById(id);
    }

    @Override
    public void deletePayment(UUID id) {
        paymentJpaRepo.deleteById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepo.findByOrderId(orderId);
    }


}
