package com.ecommerce.Inegration.PaymentGateWay.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class PaymentSession{
    private final String session_id;
    private final String session_url;
    private final String clientRefUUID;
    private final PaymentGatewayOrderModel orderModel;
    private final Instant expireAt;

}
