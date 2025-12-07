package com.ecommerce.Inegration.PaymentGateWay.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@AllArgsConstructor
@Getter
public class SessionGenerationCommand {
    private final GateWayOrderModel orderModel;
    private final Duration expireAfter;
    private final String successUrl;
    private final String failUrl;

}
