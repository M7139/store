package com.ga.store.controller;

import com.ga.store.service.OrderNotificationService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
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
    public SseEmitter subscribeToOrderUpdates(
            Authentication authentication) {

        String email =
                authentication.getName();

        return orderNotificationService.subscribe(
                email
        );
    }
}