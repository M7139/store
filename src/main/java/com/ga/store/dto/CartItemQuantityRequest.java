package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public class CartItemQuantityRequest {

    @Schema(
            description = "New quantity for the cart item",
            example = "3"
    )
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public CartItemQuantityRequest() {
    }

    public CartItemQuantityRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}