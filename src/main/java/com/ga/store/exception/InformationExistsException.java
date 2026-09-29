package com.ga.store.exception;

public class InformationExistsException extends RuntimeException{
    public InformationExistsException(String message){
        super(message);
    }
}
