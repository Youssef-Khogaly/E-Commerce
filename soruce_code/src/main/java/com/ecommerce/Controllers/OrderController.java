package com.ecommerce.Controllers;


import com.ecommerce.DTO.OrderDTOView;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.security.User.CustomUserDetails;
import com.ecommerce.services.interfaces.OrderService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
    ResponseEntity<List<OrderDTOView>>getOrders(@AuthenticationPrincipal CustomUserDetails user){
        long cust_id = user.getId();

        List<OrderDTOView> orderListViews = orderService.getOrders(cust_id);

        return ResponseEntity.ok(orderListViews);
    }
    @GetMapping("/{id}")
    ResponseEntity<?>getOrder(@PathVariable(required = true) @NotEmpty @NotNull String id,@AuthenticationPrincipal CustomUserDetails user){
        UUID idUUID;
        long cust_id = user.getId();
        try{
            idUUID= UUID.fromString(id);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
        OrderDTOView orderDTOView = orderService.getOrder(cust_id,idUUID);

        return ResponseEntity.ok(orderDTOView);
    }
    @PostMapping("/{id}/cancel")
    ResponseEntity<Void> cancelOrder(@PathVariable @NotEmpty @NotNull String id,@AuthenticationPrincipal CustomUserDetails user){
        UUID idUUID;
        long cust_id = user.getId();
        try{
            idUUID= UUID.fromString(id);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
        orderService.cancelOrder(cust_id,idUUID);

        return ResponseEntity.noContent().build();
    }
}
