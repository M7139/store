package com.ga.store.service;

import com.ga.store.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles real-time order status notifications using Server-Sent Events.
 * Notifications are sent only after the corresponding database
 * transaction commits successfully.
 */
@Service
public class OrderNotificationService {

    private final Map<String, List<SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    /**
     * Creates an SSE connection for a user.
     * Connection lists support concurrent subscriptions,
     * disconnections and notification delivery.
     *
     * @param email authenticated user's email
     * @return SSE emitter connection
     */
    public SseEmitter subscribe(String email) {

        SseEmitter emitter = new SseEmitter(0L);

        emitters.compute(email, (key, existing) -> {

            List<SseEmitter> userEmitters =
                    existing == null
                            ? new CopyOnWriteArrayList<>()
                            : existing;

            userEmitters.add(emitter);

            return userEmitters;
        });

        emitter.onCompletion(() ->
                removeEmitter(
                        email,
                        emitter
                )
        );

        emitter.onTimeout(() ->
                removeEmitter(
                        email,
                        emitter
                )
        );

        emitter.onError(error ->
                removeEmitter(
                        email,
                        emitter
                )
        );

        return emitter;
    }

    /**
     * Schedules an order status event for the user's active connections.
     * The event is sent after the current database transaction commits.
     * If no transaction is active, delivery is attempted immediately.
     *
     * @param email customer email
     * @param orderId order ID
     * @param status new order status
     */
    public void sendOrderStatusUpdate(
            String email,
            Long orderId,
            OrderStatus status) {

        TransactionActions.afterCommit(() ->
                sendCommittedUpdate(
                        email,
                        orderId,
                        status
                )
        );
    }

    /**
     * Sends a committed order status to all active connections
     * belonging to the customer.
     * Failed or closed connections are removed.
     *
     * @param email customer email
     * @param orderId order ID
     * @param status committed order status
     */
    private void sendCommittedUpdate(
            String email,
            Long orderId,
            OrderStatus status) {

        List<SseEmitter> userEmitters =
                emitters.get(email);

        if (userEmitters == null) {
            return;
        }

        List<SseEmitter> failedEmitters =
                new ArrayList<>();

        for (SseEmitter emitter : userEmitters) {

            try {

                emitter.send(
                        SseEmitter.event()
                                .name("order-status")
                                .data(
                                        "Order #"
                                                + orderId
                                                + " status changed to "
                                                + status
                                )
                );

            } catch (IOException | IllegalStateException exception) {

                failedEmitters.add(emitter);
            }
        }

        failedEmitters.forEach(emitter ->
                removeEmitter(
                        email,
                        emitter
                )
        );
    }

    /**
     * Removes a closed or failed SSE connection.
     * The user's entry is removed when no connections remain.
     * Removal is coordinated with concurrent subscriptions.
     *
     * @param email customer email
     * @param emitter SSE connection
     */
    private void removeEmitter(
            String email,
            SseEmitter emitter) {

        emitters.computeIfPresent(email, (key, userEmitters) -> {

            userEmitters.remove(emitter);

            return userEmitters.isEmpty()
                    ? null
                    : userEmitters;
        });
    }
}