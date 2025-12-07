package com.ecommerce.Controllers;

import com.ecommerce.DTO.Requests.CheckoutReq;
import com.ecommerce.DTO.Requests.CheckoutResponse;
import com.ecommerce.security.User.CustomUserDetails;
import com.ecommerce.services.checkout.CheckoutService;
import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/api/me/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutReq checkoutReq,@AuthenticationPrincipal CustomUserDetails user)  {
        PaymentSession ret = null;
        long cust_id = user.getId();
        ret = checkoutService.checkout(checkoutReq,cust_id);

        return ResponseEntity.ok(new CheckoutResponse(ret.getSession_url(),ret.getOrderModel().getOrder_id().toString(), ret.getExpireAt()));
    }
}
