package com.ga.store.controller;

import com.ga.store.service.OrderNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@Tag(
        name = "Notifications",
        description = "Real-time order status notifications using Server-Sent Events"
)
@SecurityRequirement(name = "bearerAuth")
public class OrderNotificationController {

    private final OrderNotificationService orderNotificationService;

    public OrderNotificationController(
            OrderNotificationService orderNotificationService) {

        this.orderNotificationService =
                orderNotificationService;
    }

    @GetMapping(
            value = "/orders",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    @Operation(
            summary = "Subscribe to order notifications",
            description = "Opens a Server-Sent Events connection for the currently authenticated customer. Order status updates are sent through this connection."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE connection opened successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            )
    })
    public SseEmitter subscribeToOrderUpdates(
            Authentication authentication) {

        String email =
                authentication.getName();

        return orderNotificationService.subscribe(
                email
        );
    }
}