package com.ecommerce.webhook.Interfaces;

import com.ecommerce.webhook.PaymentWebhookEvent;

import java.util.concurrent.RejectedExecutionException;

public interface PaymentWebhookPublisher {

    void publish(PaymentWebhookEvent event) throws RejectedExecutionException;
}
