package com.ecommerce.services.webhook.Interfaces;

import com.ecommerce.services.webhook.PaymentWebhookEvent;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentWebhookParser {


    PaymentWebhookEvent parse(HttpServletRequest request , String payload);
}
