package com.ecommerce.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/stripe")
public class webHook {


    private final String stripeHookSec;

    public webHook(@Value("${StripeWhsec}") String stripeHookSec) {
        this.stripeHookSec = stripeHookSec;
    }

    @PostMapping
    public ResponseEntity<?> hook(
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String payload
    ) {

        return ResponseEntity.ok().build();
    }

}
