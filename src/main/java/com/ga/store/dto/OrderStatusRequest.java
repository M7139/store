package com.ga.store.dto;

import com.ga.store.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {

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