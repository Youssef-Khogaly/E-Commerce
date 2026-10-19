package com.ecommerce.checkout.async;

import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;

import java.util.UUID;

public interface CheckoutSessionGenerationHandler {

    void handle(CheckoutJobCreatedEvent event);
}
