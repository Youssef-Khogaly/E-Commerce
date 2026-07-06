package com.ecommerce.services.webhook.Interfaces;

import com.ecommerce.services.webhook.PaymentWebhookEvent;

import java.util.concurrent.RejectedExecutionException;

public interface PaymentWebhookPublisher {

    void publish(PaymentWebhookEvent event) throws RejectedExecutionException;
}
