package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class OrderItemResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "3")
    private Long productId;

    @Schema(example = "Lavender Soap")
    private String productName;

    @Schema(example = "2.50")
    private BigDecimal price;

    @Schema(example = "2")
    private int quantity;

    @Schema(example = "5.00")
    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(
            Long id,
            Long productId,
            String productName,
            BigDecimal price,
            int quantity,
            BigDecimal subtotal) {

        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}