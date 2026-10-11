package com.ecommerce.webhook.Interfaces;


import com.ecommerce.webhook.PaymentWebhookEvent;

public interface PaymentWebhookHandler {
    public void handle(PaymentWebhookEvent event);
}
