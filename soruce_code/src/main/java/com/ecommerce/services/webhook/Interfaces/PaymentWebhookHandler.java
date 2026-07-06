package com.ecommerce.services.webhook.Interfaces;


import com.ecommerce.services.webhook.PaymentWebhookEvent;

public interface PaymentWebhookHandler {
    public void handle(PaymentWebhookEvent event);
}
