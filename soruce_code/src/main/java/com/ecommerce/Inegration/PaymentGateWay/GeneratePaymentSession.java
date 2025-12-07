package com.ecommerce.Inegration.PaymentGateWay;


import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;

public  interface GeneratePaymentSession {

    public <T extends SessionGenerationCommand, R extends PaymentSession> R generateSessionUrl(T command);
}
