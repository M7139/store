package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ProductReviewRequest {

    @Schema(
            description = "Rating from 1 to 5",
            example = "5",
            minimum = "1",
            maximum = "5"
    )
    @Min(
            value = 1,
            message = "Rating must be at least 1"
    )
    @Max(
            value = 5,
            message = "Rating cannot be more than 5"
    )
    private int rating;

    @Schema(
            description = "Optional review comment",
            example = "Great soap and smells very nice."
    )
    @Size(
            max = 1000,
            message = "Comment cannot be more than 1000 characters"
    )
    private String comment;

    public ProductReviewRequest() {
    }

    public ProductReviewRequest(
            int rating,
            String comment) {

        this.rating = rating;
        this.comment = comment;
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
}