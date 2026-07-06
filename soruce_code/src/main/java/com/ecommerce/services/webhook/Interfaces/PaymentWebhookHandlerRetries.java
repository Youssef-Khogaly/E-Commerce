package com.ecommerce.services.webhook.Interfaces;

public interface PaymentWebhookHandlerRetries {


    void handleWithRetries(Runnable runnable);
}
