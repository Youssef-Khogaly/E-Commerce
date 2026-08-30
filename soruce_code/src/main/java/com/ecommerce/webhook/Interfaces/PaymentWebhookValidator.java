package com.ecommerce.webhook.Interfaces;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentWebhookValidator {


    void validate(HttpServletRequest request , String payload) throws Exception;
}
