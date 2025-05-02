package com.example.agrisupply.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException; // Extend the base Spring Security exception
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception indicating a failure during the authentication process (e.g., invalid credentials).
 * Extends Spring Security's {@link AuthenticationException} and maps to HTTP status 401 Unauthorized.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED) // Maps to HTTP 401 Unauthorized
public class AuthenticationFailedException extends AuthenticationException {

    /**
     * Constructs an AuthenticationFailedException with the specified message and root cause.
     * @param msg the detail message.
     * @param cause the root cause (usually from Spring Security).
     */
    public AuthenticationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an AuthenticationFailedException with the specified message and no root cause.
     * @param msg the detail message.
     */
    public AuthenticationFailedException(String msg) {
        super(msg);
    }
}