package com.ecommerce.Controllers;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhook/stripe")
public class webHook {


    private final String stripeHookSec;
    private final PaymentHookHandler paymentHookHandler;

    public webHook(PaymentHookHandler paymentHookHandler,  @Value("${StripeWhsec}") String stripeHookSec) {
        this.paymentHookHandler = paymentHookHandler;
        this.stripeHookSec = stripeHookSec;
    }

    @PostMapping
    public ResponseEntity<?> hook(
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String payload
    ) {

        Event event = null;
        try{
             event = Webhook.constructEvent(payload,signature,stripeHookSec);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        System.out.println(payload);

        UUID orderId;
        UUID paymentId;
        switch (event.getType()){
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow(() -> new RuntimeException("stripe web hook failed parsing the payment intent"));
                 orderId = UUID.fromString(paymentIntent.getMetadata().get("OrderId"));
                 paymentId = UUID.fromString(paymentIntent.getMetadata().get("OrderPaymentId"));
                String transactionId = paymentIntent.getLatestCharge();
                paymentHookHandler.handle(orderId,paymentId,transactionId, PaymentHookHandler.PaymentEvents.PAID_SUCCESSFULLY);
                    ;
                break;
                // payment successes handle
            case "checkout.session.expired":
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(() -> new RuntimeException("stripe web hook failed parsing the session"));
                orderId = UUID.fromString(session.getMetadata().get("OrderId"));
                paymentId = UUID.fromString(session.getMetadata().get("OrderPaymentId"));
                paymentHookHandler.handle(orderId,paymentId,null, PaymentHookHandler.PaymentEvents.SESSION_EXPIRED);
                // payment canceled handle
                break;
        }
        return ResponseEntity.ok().build();
    }

}
