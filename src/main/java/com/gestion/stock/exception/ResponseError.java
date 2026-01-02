package com.gestion.stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


public class ResponseError {

    private String message;
    private HttpStatus status;

    public ResponseError(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
    public ResponseError(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
