package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ErrorResponse {

    @Schema(example = "2026-10-07T08:30:00")
    private LocalDateTime timestamp;

    @Schema(example = "404")
    private int status;

    @Schema(example = "Not Found")
    private String error;

    @Schema(example = "Order not found")
    private String message;

    @Schema(example = "/api/orders/me/100")
    private String path;

    public ErrorResponse() {
    }

    public ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path) {

        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(
            LocalDateTime timestamp) {

        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(
            int status) {

        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(
            String error) {

        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(
            String message) {

        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(
            String path) {

        this.path = path;
    }
}