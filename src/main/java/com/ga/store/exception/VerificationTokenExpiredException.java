package com.ga.store.exception;

public class VerificationTokenExpiredException extends RuntimeException {

    public VerificationTokenExpiredException(String message) {
        super(message);
    }
}