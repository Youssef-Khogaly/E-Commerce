package com.ecommerce.checkout;

import com.ecommerce.util.ErrorResponse;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.security.User.CustomUserDetails;
import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    @Operation(summary = "check out" , description = "Required Role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200" , content = @Content(schema = @Schema(implementation = CheckoutResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
            }

    )
    @RequireAuthDocs
    @PostMapping("/api/me/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutReq checkoutReq,@AuthenticationPrincipal CustomUserDetails user)  {
        PaymentSession ret = null;
        long cust_id = user.getId();
        ret = checkoutService.checkout(checkoutReq,cust_id);

        return ResponseEntity.ok(new CheckoutResponse(ret.getSession_url(),ret.getOrderModel().getOrder_id().toString(), ret.getExpireAt()));
    }
}
