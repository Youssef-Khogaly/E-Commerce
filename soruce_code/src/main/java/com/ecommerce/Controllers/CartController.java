package com.ecommerce.Controllers;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.security.User.CustomUserDetails;
import com.ecommerce.services.interfaces.CartService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/me/cart")
@Validated
public class CartController {

    private final CartService cartService;


    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@RequestParam @Positive @NotNull Long productId,
                                             @Positive @NotNull Integer quantity,
                                             @AuthenticationPrincipal CustomUserDetails user

    ){

        cartService.addToCart(user.getId(), productId, quantity);
        return ResponseEntity.ok().body(cartService.getCart(user.getId()));
    }

    @PutMapping("/items")
    public ResponseEntity<?> putToCart(@RequestParam @Positive @NotNull Long productId ,
                                       @PositiveOrZero @NotNull Integer quantity
    , @AuthenticationPrincipal CustomUserDetails user
    ){
        long cust_id = user.getId();
        cartService.putToCart(cust_id, productId,quantity);
        return ResponseEntity.ok().body(cartService.getCart(cust_id));
    }
    @DeleteMapping("/items")
    public ResponseEntity<CartDTO> deleteCartItem(@RequestParam @Positive @NotNull Long productId,@AuthenticationPrincipal CustomUserDetails user){
        long cust_id = user.getId();;
        cartService.deleteFromCart(cust_id,productId);

        return ResponseEntity.ok().body(cartService.getCart(cust_id));
    }
    @GetMapping
    public ResponseEntity<CartDTO> getCart(@AuthenticationPrincipal CustomUserDetails user) {
        long cust_id = user.getId();;
        var cart = cartService.getCart(cust_id);
        return ResponseEntity.ok(cart);
    }
}
