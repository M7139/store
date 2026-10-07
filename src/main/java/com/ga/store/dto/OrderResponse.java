package com.ga.store.dto;

import com.ga.store.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    @Schema(example = "6")
    private Long id;

    @Schema(
            description = "Current order status",
            example = "CONFIRMED"
    )
    private OrderStatus status;

    @Schema(example = "12.00")
    private BigDecimal totalAmount;

    @Schema(example = "123")
    private String house;

    @Schema(example = "456")
    private String road;

    @Schema(example = "789")
    private String block;

    @Schema(example = "Manama")
    private String area;

    @Schema(example = "+97333123456")
    private String phoneNumber;

    @Schema(description = "Products included in the order")
    private List<OrderItemResponse> items;

    @Schema(example = "2026-10-07T08:30:00")
    private LocalDateTime createdAt;

    public OrderResponse() {
    }

    public OrderResponse(
            Long id,
            OrderStatus status,
            BigDecimal totalAmount,
            String house,
            String road,
            String block,
            String area,
            String phoneNumber,
            List<OrderItemResponse> items,
            LocalDateTime createdAt) {

        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.house = house;
        this.road = road;
        this.block = block;
        this.area = area;
        this.phoneNumber = phoneNumber;
        this.items = items;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}