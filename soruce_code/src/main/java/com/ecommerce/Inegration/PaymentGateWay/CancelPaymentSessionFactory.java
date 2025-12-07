package com.ecommerce.Inegration.PaymentGateWay;

import com.ecommerce.Inegration.PaymentGateWay.Stripe.StripeCancelSession;
import com.ecommerce.Inegration.PaymentGateWay.Stripe.StripeGeneratePaymentSession;
import com.ecommerce.entities.Payments.PaymentMethod;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CancelPaymentSessionFactory {
    private final ApplicationContext context;


    public CancelSession getCancelSessionStratigy(PaymentMethod method){
        return  switch (method){
            case Stripe -> context.getBean(StripeCancelSession.class);
            default -> throw new IllegalArgumentException("illegal payment method");
        };

    }
}
