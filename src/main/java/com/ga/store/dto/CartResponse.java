package com.ga.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(description = "Items currently in the cart")
    private List<CartItemResponse> items;

    @Schema(example = "12.50")
    private BigDecimal total;

    public CartResponse() {
    }

    public CartResponse(
            Long id,
            List<CartItemResponse> items,
            BigDecimal total) {

        this.id = id;
        this.items = items;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}