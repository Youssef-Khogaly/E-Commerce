package com.ecommerce.Inegration.PaymentGateWay.Model;

import com.ecommerce.entities.Payments.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@AllArgsConstructor
@Getter
public class SessionGenerationCommand {
    private final PaymentGatewayOrderModel orderModel;
    private final Duration expireAfter;
    private final String successUrl;
    private final String failUrl;
    private final PaymentMethod method;

}
