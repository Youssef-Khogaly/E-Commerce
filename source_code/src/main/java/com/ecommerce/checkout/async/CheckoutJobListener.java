package com.ecommerce.checkout.async;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@AllArgsConstructor
@Component
public class CheckoutJobListener {
    private final CheckoutSessionGenerationPublisher sessionGenerationPublisher;


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCheckoutJobCreated(CheckoutJobCreatedEvent event){
        sessionGenerationPublisher.publish(event);
    }
}
