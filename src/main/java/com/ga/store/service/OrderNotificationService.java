package com.ga.store.service;

import com.ga.store.enums.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderNotificationService {

    private final Map<String, List<SseEmitter>> emitters =
            new ConcurrentHashMap<>();

    public SseEmitter subscribe(String email) {

        SseEmitter emitter =
                new SseEmitter(0L);

        emitters.computeIfAbsent(
                email,
                key -> new ArrayList<>()
        ).add(emitter);

        emitter.onCompletion(() ->
                removeEmitter(
                        email,
                        emitter
                ));

        emitter.onTimeout(() ->
                removeEmitter(
                        email,
                        emitter
                ));

        emitter.onError(error ->
                removeEmitter(
                        email,
                        emitter
                ));

        return emitter;
    }

    public void sendOrderStatusUpdate(
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
                                .name(
                                        "order-status"
                                )
                                .data(
                                        "Order #"
                                                + orderId
                                                + " status changed to "
                                                + status
                                )
                );

            } catch (IOException exception) {

                failedEmitters.add(
                        emitter
                );
            }
        }

        userEmitters.removeAll(
                failedEmitters
        );
    }

    private void removeEmitter(
            String email,
            SseEmitter emitter) {

        List<SseEmitter> userEmitters =
                emitters.get(email);

        if (userEmitters == null) {
            return;
        }

        userEmitters.remove(
                emitter
        );

        if (userEmitters.isEmpty()) {
            emitters.remove(
                    email
            );
        }
    }
}