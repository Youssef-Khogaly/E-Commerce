package com.ecommerce.webhook.Interfaces;

import com.ecommerce.webhook.PaymentWebhookEvent;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentWebhookParser {


    PaymentWebhookEvent parse(HttpServletRequest request , String payload);
}
