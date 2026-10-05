package com.ga.store.dto;

import com.ga.store.enums.PaymentMethod;
import com.ga.store.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            Long orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDateTime createdAt) {

        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id) {

        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(
            Long orderId) {

        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {

        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {

        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(
            PaymentStatus status) {

        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }
}