package com.ga.store.service;

import com.ga.store.dto.PaymentRequest;
import com.ga.store.dto.PaymentResponse;
import com.ga.store.enums.OrderStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Order;
import com.ga.store.model.Payment;
import com.ga.store.model.User;
import com.ga.store.repository.OrderRepository;
import com.ga.store.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserService userService) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    public PaymentResponse createPayment(
            String email,
            Long orderId,
            PaymentRequest request) {

        User user =
                userService.getUserByEmail(email);

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Order not found"
                                ));

        if (!order.getUser().getId()
                .equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Order not found"
            );
        }

        if (order.getStatus()
                != OrderStatus.PENDING) {

            throw new InformationExistsException(
                    "Payment can only be added to a pending order"
            );
        }

        if (paymentRepository.existsByOrder(order)) {

            throw new InformationExistsException(
                    "Payment already exists for this order"
            );
        }

        Payment payment =
                new Payment(
                        order,
                        user,
                        order.getTotalAmount(),
                        request.getPaymentMethod()
                );

        Payment savedPayment =
                paymentRepository.save(payment);

        return createPaymentResponse(
                savedPayment
        );
    }

    public PaymentResponse getPaymentByOrder(
            String email,
            Long orderId) {

        User user =
                userService.getUserByEmail(email);

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Order not found"
                                ));

        if (!order.getUser().getId()
                .equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Order not found"
            );
        }

        Payment payment =
                paymentRepository.findByOrder(order)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Payment not found"
                                ));

        return createPaymentResponse(
                payment
        );
    }

    public List<PaymentResponse> getPaymentsByUser(
            String email) {

        User user =
                userService.getUserByEmail(email);

        return paymentRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::createPaymentResponse)
                .toList();
    }

    private PaymentResponse createPaymentResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}