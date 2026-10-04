package com.ga.store.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private Long id;
    private List<CartItemResponse> items;
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