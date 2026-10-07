package com.ga.store.controller;

import com.ga.store.dto.PaymentRequest;
import com.ga.store.dto.PaymentResponse;
import com.ga.store.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payments",
        description = "Customer payment management and admin payment viewing"
)
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    @Operation(
            summary = "Create payment",
            description = "Creates a payment for an order belonging to the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payment information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Payment already exists or order cannot be paid"
            )
    })
    public ResponseEntity<PaymentResponse> createPayment(
            Authentication authentication,

            @Parameter(
                    description = "Order ID",
                    example = "1"
            )
            @PathVariable Long orderId,

            @Valid @RequestBody PaymentRequest request) {

        String email =
                authentication.getName();

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
    @Operation(
            summary = "Get payment by order",
            description = "Returns the payment associated with an order belonging to the current customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order or payment not found"
            )
    })
    public ResponseEntity<PaymentResponse> getPaymentByOrder(
            Authentication authentication,

            @Parameter(
                    description = "Order ID",
                    example = "1"
            )
            @PathVariable Long orderId) {

        String email =
                authentication.getName();

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
    @Operation(
            summary = "Get my payments",
            description = "Returns all payments belonging to the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            Authentication authentication) {

        String email =
                authentication.getName();

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
    @Operation(
            summary = "Get all payments",
            description = "Returns all customer payments. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payments returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            )
    })
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        List<PaymentResponse> response =
                paymentService.getAllPayments();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}