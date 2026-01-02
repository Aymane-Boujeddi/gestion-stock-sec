package com.gestion.stock.exception;

/**
 * Exception thrown when a user tries to login without an assigned role
 */
public class RoleNotAssignedException extends RuntimeException {
    public RoleNotAssignedException(String message) {
        super(message);
    }
}


