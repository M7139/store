package com.ga.store.exception;

import com.ga.store.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler(InformationExistsException.class)
    public ResponseEntity<ErrorResponse> handleInformationExistsException(
            InformationExistsException exception,
            HttpServletRequest request) {

        logger.warn(
                "Conflict error: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.CONFLICT,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException exception,
            HttpServletRequest request) {

        logger.warn(
                "Authentication error: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> handleInactiveAccountException(
            InactiveAccountException exception,
            HttpServletRequest request) {

        logger.warn(
                "Inactive account access blocked: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.FORBIDDEN,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(
            EmailNotVerifiedException exception,
            HttpServletRequest request) {

        logger.warn(
                "Unverified account access blocked: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.FORBIDDEN,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(InformationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInformationNotFoundException(
            InformationNotFoundException exception,
            HttpServletRequest request) {

        logger.warn(
                "Resource not found: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpiredException(
            VerificationTokenExpiredException exception,
            HttpServletRequest request) {

        logger.warn(
                "Verification token error: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException exception,
            HttpServletRequest request) {

        logger.warn(
                "Rate limit exceeded: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.TOO_MANY_REQUESTS,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        logger.warn(
                "Invalid request: {}",
                exception.getMessage()
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        logger.warn(
                "Request validation failed"
        );

        StringBuilder message =
                new StringBuilder();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    if (!message.isEmpty()) {
                        message.append("; ");
                    }

                    message.append(
                            error.getField()
                    );

                    message.append(": ");

                    message.append(
                            error.getDefaultMessage()
                    );
                });

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        message.toString(),
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception exception,
            HttpServletRequest request) {

        logger.error(
                "Unexpected server error",
                exception
        );

        ErrorResponse response =
                createErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred",
                        request.getRequestURI()
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ErrorResponse createErrorResponse(
            HttpStatus status,
            String message,
            String path) {

        return new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}