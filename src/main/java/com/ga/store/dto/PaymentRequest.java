package com.ga.store.dto;

import com.ga.store.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(
            PaymentMethod paymentMethod) {

        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod) {

        this.paymentMethod = paymentMethod;
    }
}