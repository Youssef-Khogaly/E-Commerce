package com.ecommerce.Inegration.PaymentGateWay;


import com.ecommerce.Inegration.PaymentGateWay.Interfaces.GeneratePaymentSession;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.Inegration.PaymentGateWay.Stripe.StripeGeneratePaymentSession;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GeneratePaymentSessionFactory {

    private final ApplicationContext context;
    public GeneratePaymentSession getStratigy(PaymentMethod method){
        return  switch (method){
            case Stripe -> context.getBean(StripeGeneratePaymentSession.class);
            default -> throw new IllegalArgumentException("illegal payment method");
        };

    }

}
