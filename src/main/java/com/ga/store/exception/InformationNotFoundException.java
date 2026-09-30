package com.ga.store.exception;

public class InformationNotFoundException extends RuntimeException {

    public InformationNotFoundException(String message) {
        super(message);
    }
}