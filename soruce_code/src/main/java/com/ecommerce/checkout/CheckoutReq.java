package com.ecommerce.checkout;

import com.ecommerce.orders.dtos.ShippingDTO;
import com.ecommerce.Payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

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