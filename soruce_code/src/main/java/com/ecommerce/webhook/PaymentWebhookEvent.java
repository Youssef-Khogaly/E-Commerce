package com.ecommerce.webhook;

import com.ecommerce.util.Money;
import com.ecommerce.Payment.entity.PaymentMethod;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class PaymentWebhookEvent {

    private final String id;
    private final PaymentMethod provider;
    private final PaymentEvents event;
    private final String transactionId;
    private final String sessionId;
    private final Money totalAmount;
    private final long provider_created;
    private final Map<String,String>metaData;
}
