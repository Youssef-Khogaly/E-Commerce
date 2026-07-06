package com.ecommerce.Controllers;

import com.ecommerce.services.webhook.Interfaces.PaymentWebhookParser;
import com.ecommerce.services.webhook.Interfaces.PaymentWebhookPublisher;
import com.ecommerce.services.webhook.Interfaces.PaymentWebhookValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.RejectedExecutionException;

@RestController
@RequestMapping("/api/webhook/stripe")
@AllArgsConstructor
public class webHook {
    private final PaymentWebhookPublisher paymentWebhookPublisher;
    private final PaymentWebhookParser paymentWebhookParser;
    private final PaymentWebhookValidator paymentWebhookValidator;



    @PostMapping
    public ResponseEntity<?> hook(@RequestBody @NotBlank String rawBody , HttpServletRequest request
    ) {
        try{
            paymentWebhookValidator.validate(request,rawBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var event = paymentWebhookParser.parse(request,rawBody);
        try {
            paymentWebhookPublisher.publish(event);
        } catch (RejectedExecutionException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.ok().build();
    }

}
