package com.ecommerce.services.Exceptions;

public class IllegalPaymentState extends PaymentException {
    public IllegalPaymentState(String message) {
        super(message);
    }
}
