package com.ecommerce.Inegration.PaymentGateWay.Stripe;

import com.ecommerce.Inegration.PaymentGateWay.Interfaces.CancelSession;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StripeCancelSession implements CancelSession {

    public StripeCancelSession(@Value("${StripeApisec}") String apiKey){
        Stripe.apiKey = apiKey;
    }

    @Override
    public void cancelSession(String sessionId) {

        try{
            Session session = Session.retrieve(sessionId);
            session.expire();
        } catch (StripeException e) {
            log.error(e.getMessage());
            throw new RuntimeException("couldn't expire stripe session , sessionID:" + sessionId);
        }

    }
}
