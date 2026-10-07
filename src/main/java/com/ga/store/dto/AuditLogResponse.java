package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class AuditLogResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(
            description = "ID of the user who performed the action",
            example = "1"
    )
    private Long userId;

    @Schema(example = "ORDER_STATUS_CHANGED")
    private String action;

    @Schema(
            example = "Changed order 6 status to PROCESSING"
    )
    private String description;

    @Schema(example = "2026-10-07T08:30:00")
    private LocalDateTime createdAt;

    public AuditLogResponse() {
    }

    public AuditLogResponse(
            Long id,
            Long userId,
            String action,
            String description,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId) {

        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(
            String action) {

        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}