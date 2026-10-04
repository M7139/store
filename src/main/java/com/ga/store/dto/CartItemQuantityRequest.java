package com.ga.store.dto;

import jakarta.validation.constraints.Min;

public class CartItemQuantityRequest {

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