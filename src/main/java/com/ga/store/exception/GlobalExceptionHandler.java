package com.ga.store.exception;

import com.ga.store.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;

/**
 * Converts application and request exceptions into consistent API responses.
 * Expected client errors receive appropriate HTTP status codes,
 * while unexpected failures return a generic server error.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles duplicate information and invalid business-state changes.
     *
     * @param exception conflict exception
     * @param request current HTTP request
     * @return HTTP 409 response
     */
    @ExceptionHandler(InformationExistsException.class)
    public ResponseEntity<ErrorResponse> handleInformationExistsException(
            InformationExistsException exception,
            HttpServletRequest request) {

        logger.warn(
                "Conflict error: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    /**
     * Handles incorrect login credentials or current passwords.
     *
     * @param exception authentication exception
     * @param request current HTTP request
     * @return HTTP 401 response
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException exception,
            HttpServletRequest request) {

        logger.warn(
                "Authentication error: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * Handles attempts to use an inactive account.
     *
     * @param exception inactive account exception
     * @param request current HTTP request
     * @return HTTP 403 response
     */
    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> handleInactiveAccountException(
            InactiveAccountException exception,
            HttpServletRequest request) {

        logger.warn(
                "Inactive account access blocked: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Handles attempts to log in before email verification.
     *
     * @param exception email verification exception
     * @param request current HTTP request
     * @return HTTP 403 response
     */
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerifiedException(
            EmailNotVerifiedException exception,
            HttpServletRequest request) {

        logger.warn(
                "Unverified account access blocked: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Handles method-level authorization failures, such as a customer
     * attempting to access an administrator operation.
     * Security-filter failures are handled separately by Spring Security.
     *
     * @param exception access denial exception
     * @param request current HTTP request
     * @return HTTP 403 response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {

        logger.warn(
                "Access denied for request: {}",
                request.getRequestURI()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }

    /**
     * Handles requests for information that does not exist.
     *
     * @param exception missing information exception
     * @param request current HTTP request
     * @return HTTP 404 response
     */
    @ExceptionHandler(InformationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInformationNotFoundException(
            InformationNotFoundException exception,
            HttpServletRequest request) {

        logger.warn(
                "Resource not found: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Handles expired verification or password reset tokens.
     *
     * @param exception expired token exception
     * @param request current HTTP request
     * @return HTTP 400 response
     */
    @ExceptionHandler(VerificationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenExpiredException(
            VerificationTokenExpiredException exception,
            HttpServletRequest request) {

        logger.warn(
                "Verification token error: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles requests that exceed the allowed rate limit.
     *
     * @param exception rate limit exception
     * @param request current HTTP request
     * @return HTTP 429 response
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException exception,
            HttpServletRequest request) {

        logger.warn(
                "Rate limit exceeded: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.TOO_MANY_REQUESTS
        );
    }

    /**
     * Handles invalid values rejected by application code.
     *
     * @param exception invalid argument exception
     * @param request current HTTP request
     * @return HTTP 400 response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request) {

        logger.warn(
                "Invalid request: {}",
                exception.getMessage()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles validation failures in request DTOs.
     *
     * @param exception request validation exception
     * @param request current HTTP request
     * @return HTTP 400 response containing field validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        logger.warn(
                "Request validation failed"
        );

        StringBuilder message = new StringBuilder();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    if (!message.isEmpty()) {
                        message.append("; ");
                    }

                    message.append(error.getField());
                    message.append(": ");
                    message.append(error.getDefaultMessage());
                });

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message.toString(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles malformed JSON, invalid parameter types,
     * missing request parameters and missing multipart fields.
     * Internal parsing details are not included in the response.
     *
     * @param exception malformed request exception
     * @param request current HTTP request
     * @return HTTP 400 response
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ErrorResponse> handleMalformedRequest(
            Exception exception,
            HttpServletRequest request) {

        logger.warn(
                "Malformed request at {}: {}",
                request.getRequestURI(),
                exception.getClass().getSimpleName()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid or missing request value",
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handles uploads that exceed the configured multipart size limit.
     *
     * @param exception upload size exception
     * @param request current HTTP request
     * @return HTTP 413 response
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadSize(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request) {

        logger.warn(
                "Upload size limit exceeded at {}",
                request.getRequestURI()
        );

        HttpStatus status = HttpStatus.valueOf(413);

        ErrorResponse response = createErrorResponse(
                status,
                "File exceeds the upload limit",
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                status
        );
    }

    /**
     * Handles database constraint conflicts, including duplicate
     * records and attempts to delete records that are still referenced.
     * Database constraint names and SQL details are not exposed.
     *
     * @param exception database integrity exception
     * @param request current HTTP request
     * @return HTTP 409 response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {

        logger.warn(
                "Database constraint conflict at {}",
                request.getRequestURI()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                "This change conflicts with existing data. "
                        + "Check for duplicate values or records "
                        + "that are still referenced.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    /**
     * Handles conflicting database updates and lock failures.
     *
     * @param exception concurrent database operation exception
     * @param request current HTTP request
     * @return HTTP 409 response
     */
    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentChange(
            ConcurrencyFailureException exception,
            HttpServletRequest request) {

        logger.warn(
                "Concurrent database operation failed at {}",
                request.getRequestURI()
        );

        ErrorResponse response = createErrorResponse(
                HttpStatus.CONFLICT,
                "The operation conflicted with another request. "
                        + "Refresh and try again.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }

    /**
     * Handles exceptions without a more specific application handler.
     * Spring web exceptions retain their designated HTTP status.
     * Unexpected failures are logged and return a generic HTTP 500 response.
     *
     * @param exception unhandled exception
     * @param request current HTTP request
     * @return error response with the appropriate HTTP status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception exception,
            HttpServletRequest request) {

        if (exception instanceof org.springframework.web.ErrorResponse webError) {

            HttpStatus status = HttpStatus.resolve(
                    webError.getStatusCode().value()
            );

            if (status != null && status.is4xxClientError()) {

                logger.warn(
                        "Request failed at {} with status {}",
                        request.getRequestURI(),
                        status.value()
                );

                ErrorResponse response = createErrorResponse(
                        status,
                        status.getReasonPhrase(),
                        request.getRequestURI()
                );

                return new ResponseEntity<>(
                        response,
                        status
                );
            }
        }

        logger.error(
                "Unexpected server error",
                exception
        );

        ErrorResponse response = createErrorResponse(
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