package com.ga.store.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler(InformationExistsException.class)
    public ResponseEntity<Map<String, String>> handleInformationExistsException(
            InformationExistsException exception) {

        logger.warn(
                "Conflict error: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentialsException(
            InvalidCredentialsException exception) {

        logger.warn(
                "Authentication error: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<Map<String, String>> handleInactiveAccountException(
            InactiveAccountException exception) {

        logger.warn(
                "Inactive account access blocked: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, String>> handleEmailNotVerifiedException(
            EmailNotVerifiedException exception) {

        logger.warn(
                "Unverified account access blocked: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(InformationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInformationNotFoundException(
            InformationNotFoundException exception) {

        logger.warn(
                "Resource not found: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleVerificationTokenExpiredException(
            VerificationTokenExpiredException exception) {

        logger.warn(
                "Verification token error: {}",
                exception.getMessage()
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                exception.getMessage()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException exception) {

        logger.warn(
                "Request validation failed"
        );

        Map<String, String> errors =
                new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        ));

        return new ResponseEntity<>(
                errors,
                HttpStatus.BAD_REQUEST
        );
    }
}