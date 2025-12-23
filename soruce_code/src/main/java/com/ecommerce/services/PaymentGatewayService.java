package com.ecommerce.services;

import com.ecommerce.Inegration.PaymentGateWay.CancelPaymentSessionFactory;
import com.ecommerce.Inegration.PaymentGateWay.GeneratePaymentSessionFactory;
import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentGatewayService implements IPaymentGatewayService {
    private GeneratePaymentSessionFactory generatePaymentSessionFactory;
    private CancelPaymentSessionFactory cancelPaymentSessionFactory;
    @Override
    public void cancelSession(String sessionId, PaymentMethod method) {
        cancelPaymentSessionFactory.getCancelSessionStratigy(method).cancelSession(sessionId);
    }

    @Override
    public <T extends SessionGenerationCommand, R extends PaymentSession> R generateSessionUrl(T command) {
        return generatePaymentSessionFactory.getStratigy(command.getMethod()).generateSessionUrl(command);
    }
}
