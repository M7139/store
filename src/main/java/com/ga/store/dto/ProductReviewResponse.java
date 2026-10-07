package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ProductReviewResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "3")
    private Long productId;

    @Schema(example = "2")
    private Long userId;

    @Schema(example = "Mohamed Rashad")
    private String userName;

    @Schema(example = "5")
    private int rating;

    @Schema(example = "Great soap and smells very nice.")
    private String comment;

    @Schema(example = "2026-10-07T08:30:00")
    private LocalDateTime createdAt;

    @Schema(example = "2026-10-07T08:45:00")
    private LocalDateTime updatedAt;

    public ProductReviewResponse() {
    }

    public ProductReviewResponse(
            Long id,
            Long productId,
            Long userId,
            String userName,
            int rating,
            String comment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(
            Long productId) {

        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(
            Long userId) {

        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(
            String userName) {

        this.userName = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(
            int rating) {

        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(
            String comment) {

        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }
}