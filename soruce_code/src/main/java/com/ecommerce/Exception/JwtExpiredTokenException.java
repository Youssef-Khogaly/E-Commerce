package com.ecommerce.Exception;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredTokenException extends AuthenticationException {
    public JwtExpiredTokenException(String message) {
        super(message);
    }
}
