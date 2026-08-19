package com.ecommerce.Payment.exceptions;

public class IllegalPaymentState extends PaymentException {
    public IllegalPaymentState(String message) {
        super(message);
    }
}
