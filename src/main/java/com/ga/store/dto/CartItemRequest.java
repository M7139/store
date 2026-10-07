package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartItemRequest {

    @Schema(
            description = "Product ID",
            example = "1"
    )
    @NotNull(message = "Product id is required")
    private Long productId;

    @Schema(
            description = "Quantity to add to the cart",
            example = "2"
    )
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public CartItemRequest() {
    }

    public CartItemRequest(
            Long productId,
            int quantity) {

        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}