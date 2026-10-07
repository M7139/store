package com.ga.store.dto;

import com.ga.store.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {

    @Schema(
            description = "New order status",
            example = "PROCESSING",
            allowableValues = {
                    "PENDING",
                    "CONFIRMED",
                    "PROCESSING",
                    "SHIPPED",
                    "DELIVERED",
                    "CANCELLED"
            }
    )
    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatusRequest() {
    }

    public OrderStatusRequest(
            OrderStatus status) {

        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(
            OrderStatus status) {

        this.status = status;
    }
}