package com.ga.store.dto;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private Long userId;
    private String action;
    private String description;
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