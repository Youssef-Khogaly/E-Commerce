package com.ecommerce.webhook.Interfaces;

public interface PaymentWebhookHandlerRetries {


    void handleWithRetries(Runnable runnable);
}
