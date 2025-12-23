package com.ecommerce.services.interfaces;

import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import com.ecommerce.entities.Payments.PaymentMethod;

public interface IPaymentGatewayService{
    public void cancelSession(String sessionId , PaymentMethod method);
    public <T extends SessionGenerationCommand, R extends PaymentSession> R generateSessionUrl(T command);
}
