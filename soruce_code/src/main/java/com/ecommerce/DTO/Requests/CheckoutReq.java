package com.ecommerce.DTO.Requests;

import com.ecommerce.DTO.CartItemReq;
import com.ecommerce.DTO.ShippingDTO;
import com.ecommerce.entities.Payments.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CheckoutReq(
        ShippingDTO shipping,
        @NotNull(message = "Payment method required") PaymentMethod paymentMethod
){

    @Override
    public String toString() {
        return "CheckoutReq{" +
                "shipping=" + shipping +
                ", paymentMethod=" + paymentMethod;
    }
};