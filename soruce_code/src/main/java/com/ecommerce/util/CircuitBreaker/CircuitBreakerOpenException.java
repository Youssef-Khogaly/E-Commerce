package com.ecommerce.util.CircuitBreaker;

public class CircuitBreakerOpenException extends RuntimeException {

    public CircuitBreakerOpenException(Throwable cause) {
        super(cause);
    }

    public CircuitBreakerOpenException(String message) {
        super(message);
    }
}
