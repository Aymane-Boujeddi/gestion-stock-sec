package com.gestion.stock.exception;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String Field){
        super(Field + " is already existing ");
    }
}
