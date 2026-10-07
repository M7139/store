package com.ga.store.controller;

import com.ga.store.dto.OrderResponse;
import com.ga.store.dto.OrderStatusRequest;
import com.ga.store.model.User;
import com.ga.store.service.AuditLogService;
import com.ga.store.service.OrderService;
import com.ga.store.service.UserService;
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
@RequestMapping("/api/orders")
@Tag(
        name = "Orders",
        description = "Customer checkout, order history, cancellation and admin order management"
)
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public OrderController(
            OrderService orderService,
            UserService userService,
            AuditLogService auditLogService) {

        this.orderService = orderService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Checkout cart",
            description = "Creates an order from the current user's cart, saves the delivery address snapshot and reduces product stock."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address or product not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Cart is empty, product is inactive or insufficient stock is available"
            )
    })
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication) {

        String email =
                authentication.getName();

        OrderResponse response =
                orderService.checkout(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get my orders",
            description = "Returns the order history of the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        String email =
                authentication.getName();

        List<OrderResponse> response =
                orderService.getOrdersByUser(
                        email
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/me/{orderId}")
    @Operation(
            summary = "Get my order by ID",
            description = "Returns a specific order belonging to the currently authenticated customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order returned successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )
    })
    public ResponseEntity<OrderResponse> getMyOrderById(
            Authentication authentication,

            @Parameter(
                    description = "Order ID",
                    example = "1"
            )
            @PathVariable Long orderId) {

        String email =
                authentication.getName();

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
    @Operation(
            summary = "Cancel my order",
            description = "Cancels the current user's order if cancellation is allowed and restores the product stock."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order cancelled successfully"
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
                    description = "Order can no longer be cancelled"
            )
    })
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,

            @Parameter(
                    description = "Order ID",
                    example = "1"
            )
            @PathVariable Long orderId) {

        String email =
                authentication.getName();

        User user =
                userService.getUserByEmail(
                        email
                );

        OrderResponse response =
                orderService.cancelOrder(
                        email,
                        orderId
                );

        auditLogService.createAuditLog(
                user.getId(),
                "ORDER_CANCELLED",
                "Cancelled order "
                        + orderId
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all orders",
            description = "Returns all customer orders. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders returned successfully"
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
    @Operation(
            summary = "Update order status",
            description = "Moves an order through the allowed order status workflow. Admin access only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status information"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Admin access required"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Invalid order status transition"
            )
    })
    public ResponseEntity<OrderResponse> updateOrderStatus(
            Authentication authentication,

            @Parameter(
                    description = "Order ID",
                    example = "1"
            )
            @PathVariable Long orderId,

            @Valid @RequestBody OrderStatusRequest request) {

        User admin =
                userService.getUserByEmail(
                        authentication.getName()
                );

        OrderResponse response =
                orderService.updateOrderStatus(
                        orderId,
                        request
                );

        auditLogService.createAuditLog(
                admin.getId(),
                "ORDER_STATUS_CHANGED",
                "Changed order "
                        + orderId
                        + " status to "
                        + response.getStatus()
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}