package com.ga.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ProductReviewRequest {

    @Min(
            value = 1,
            message = "Rating must be at least 1"
    )
    @Max(
            value = 5,
            message = "Rating cannot be more than 5"
    )
    private int rating;

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