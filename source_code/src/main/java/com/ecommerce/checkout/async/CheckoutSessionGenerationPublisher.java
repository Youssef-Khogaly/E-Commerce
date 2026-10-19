package com.ecommerce.checkout.async;

import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import org.springframework.transaction.annotation.Transactional;

import java.util.Deque;

public interface CheckoutSessionGenerationPublisher {


    void publish(CheckoutJobCreatedEvent event);
}
