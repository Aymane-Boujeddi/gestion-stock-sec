package com.gestion.stock.exception;

public class InsuffissientStockException extends RuntimeException {
    public InsuffissientStockException(String message) {
        super(message);
    }
}
