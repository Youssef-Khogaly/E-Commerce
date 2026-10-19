package com.ecommerce.checkout;

import com.ecommerce.checkout.async.*;
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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CheckoutJobViewMapper checkoutJobViewMapper;
    @Operation(summary = "check out" , description = "Required Role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "202" , content = @Content(schema = @Schema(implementation = CheckoutJobView.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
            }

    )
    @RequireAuthDocs
    @PostMapping("/api/me/checkout")
    public ResponseEntity<CheckoutJobView> checkout(@RequestBody @Valid CheckoutReq checkoutReq, @AuthenticationPrincipal CustomUserDetails user)  {
        CheckoutJob ret = null;
        long cust_id = user.getId();
        ret = checkoutService.checkout(checkoutReq,cust_id);
//        return ResponseEntity.accepted().body(CheckoutJobView.builder().id(UUID.randomUUID()).orderId(1L).status(CheckoutJobStatus.PROCESSING).sessionUrl("!").createdAt(Instant.now()).build());
        return ResponseEntity.accepted().body(checkoutJobViewMapper.from(ret));
    }

    @Operation(summary = "get check out job" , description = "Required Role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200" , content = @Content(schema = @Schema(implementation = CheckoutJobView.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404",description = "not found",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
            }

    )
    @RequireAuthDocs
    @GetMapping("/api/me/checkout/{id}")
    public ResponseEntity<CheckoutJobView> getJob(@PathVariable UUID id,  @AuthenticationPrincipal CustomUserDetails user)
    {
        var job = checkoutService.findCheckoutJob(id,user.getId());

        return ResponseEntity.ok(checkoutJobViewMapper.from(job));
    }
}
