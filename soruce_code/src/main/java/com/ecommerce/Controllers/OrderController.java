package com.ecommerce.Controllers;


import com.ecommerce.DTO.OrderDTOView;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.security.User.CustomUserDetails;
import com.ecommerce.services.interfaces.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me/orders")
@AllArgsConstructor
@Validated
public class OrderController{

    private final OrderService orderService;



    @GetMapping
    @Operation(summary = "retrieve current user orders" , description = "required role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true)
            }
    )
    @RequireAuthDocs
    ResponseEntity<List<OrderDTOView>>getOrders(@AuthenticationPrincipal CustomUserDetails user){
        long cust_id = user.getId();

        List<OrderDTOView> orderListViews = orderService.getOrders(cust_id);

        return ResponseEntity.ok(orderListViews);
    }

    @Operation(summary = "retrieve user's order" , description = "required role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = OrderDTOView.class)))
            }
    )
    @CommonErrorDocs
    @RequireAuthDocs
    @GetMapping("/{id}")
    ResponseEntity<OrderDTOView>getOrder(@PathVariable(required = true) @Positive @NotNull Long id, @AuthenticationPrincipal CustomUserDetails user){
        long cust_id = user.getId();

        OrderDTOView orderDTOView = orderService.getOrder(cust_id,id);

        return ResponseEntity.ok(orderDTOView);
    }

    @Operation(summary = "cancel order, only order in processing status can be cancelled" , description = "required role: Customer")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",description = "cancelled successfully")

            }
    )
    @CommonErrorDocs
    @RequireAuthDocs
    @PostMapping("/{id}/cancel")
    ResponseEntity<Void> cancelOrder(@PathVariable @NotEmpty @NotNull Long id,@AuthenticationPrincipal CustomUserDetails user){
        UUID idUUID;
        long cust_id = user.getId();

        orderService.cancelOrder(cust_id,id);

        return ResponseEntity.noContent().build();
    }
}
