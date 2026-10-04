package com.ga.store.controller;

import com.ga.store.dto.OrderResponse;
import com.ga.store.dto.OrderStatusRequest;
import com.ga.store.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService) {

        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication) {

        String email = authentication.getName();

        OrderResponse response =
                orderService.checkout(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email = authentication.getName();

        List<OrderResponse> response =
                orderService.getOrdersByUser(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/me/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email = authentication.getName();

        OrderResponse response =
                orderService.getOrderByIdForUser(
                        email,
                        orderId
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/me/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email = authentication.getName();

        OrderResponse response =
                orderService.cancelOrder(
                        email,
                        orderId
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        List<OrderResponse> response =
                orderService.getAllOrders();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PatchMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusRequest request) {

        OrderResponse response =
                orderService.updateOrderStatus(
                        orderId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}