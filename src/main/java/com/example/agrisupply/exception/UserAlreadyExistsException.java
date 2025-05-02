package com.example.agrisupply.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom runtime exception thrown during user registration when attempting to register
 * with an identifier (e.g., email) that already exists in the system.
 * Maps to HTTP status 409 Conflict.
 */
@ResponseStatus(value = HttpStatus.CONFLICT) // Maps to HTTP 409 Conflict
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     * @param message the detail message (e.g., "Email already exists").
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}