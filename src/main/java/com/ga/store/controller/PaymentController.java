package com.ga.store.controller;

import com.ga.store.dto.PaymentRequest;
import com.ga.store.dto.PaymentResponse;
import com.ga.store.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> createPayment(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) {

        String email = authentication.getName();

        PaymentResponse response =
                paymentService.createPayment(
                        email,
                        orderId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email = authentication.getName();

        PaymentResponse response =
                paymentService.getPaymentByOrder(
                        email,
                        orderId
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/me")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            Authentication authentication) {

        String email = authentication.getName();

        List<PaymentResponse> response =
                paymentService.getPaymentsByUser(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        List<PaymentResponse> response =
                paymentService.getAllPayments();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}