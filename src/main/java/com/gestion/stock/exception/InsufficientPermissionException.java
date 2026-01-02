package com.gestion.stock.exception;

/**
 * Exception thrown when a user lacks required permissions
 */
public class InsufficientPermissionException extends RuntimeException {
    public InsufficientPermissionException(String message) {
        super(message);
    }
}

