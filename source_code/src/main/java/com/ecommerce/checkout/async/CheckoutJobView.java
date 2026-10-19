package com.ecommerce.checkout.async;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CheckoutJobView(UUID id, long orderId, CheckoutJobStatus status, String sessionUrl, Instant createdAt) {
}
