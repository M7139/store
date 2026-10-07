package com.ga.store.service;

import com.ga.store.dto.PaymentRequest;
import com.ga.store.dto.PaymentResponse;
import com.ga.store.enums.OrderStatus;
import com.ga.store.enums.PaymentStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Order;
import com.ga.store.model.Payment;
import com.ga.store.model.User;
import com.ga.store.repository.OrderRepository;
import com.ga.store.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Handles order payments and payment status changes.
 */
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

    /**
     * Creates a payment for a pending order belonging to a customer.
     * The order remains locked until the transaction completes,
     * preventing its status from changing during payment creation.
     *
     * @param email authenticated customer's email
     * @param orderId order ID
     * @param request payment method information
     * @return created payment
     */
    @Transactional
    public PaymentResponse createPayment(
            String email,
            Long orderId,
            PaymentRequest request) {

        User user = userService.getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Order not found"
                        ));

        if (!order.getUser().getId().equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Order not found"
            );
        }

        if (order.getStatus() != OrderStatus.PENDING) {

            throw new InformationExistsException(
                    "Payment can only be added to a pending order"
            );
        }

        if (paymentRepository.existsByOrder(order)) {

            throw new InformationExistsException(
                    "Payment already exists for this order"
            );
        }

        Payment payment = new Payment(
                order,
                user,
                order.getTotalAmount(),
                request.getPaymentMethod()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return createPaymentResponse(savedPayment);
    }

    /**
     * Returns the payment for an order belonging to a customer.
     *
     * @param email authenticated customer's email
     * @param orderId order ID
     * @return payment response
     */
    public PaymentResponse getPaymentByOrder(
            String email,
            Long orderId) {

        User user = userService.getUserByEmail(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Order not found"
                        ));

        if (!order.getUser().getId().equals(user.getId())) {

            throw new InformationNotFoundException(
                    "Order not found"
            );
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Payment not found"
                        ));

        return createPaymentResponse(payment);
    }

    /**
     * Returns all payments belonging to a customer.
     *
     * @param email authenticated customer's email
     * @return customer payments
     */
    public List<PaymentResponse> getPaymentsByUser(
            String email) {

        User user = userService.getUserByEmail(email);

        return paymentRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::createPaymentResponse)
                .toList();
    }

    /**
     * Returns all payments for administrator use.
     *
     * @return all payments
     */
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::createPaymentResponse)
                .toList();
    }

    /**
     * Checks that an order has a pending payment before confirmation.
     * This method is called while the order is locked by the
     * order status update transaction.
     *
     * @param order order being confirmed
     * @throws InformationExistsException if the payment is missing
     *                                    or is not pending
     */
    public void requirePendingPayment(Order order) {

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new InformationExistsException(
                                "Add a payment method before confirming the order"
                        ));

        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new InformationExistsException(
                    "Order requires a pending payment"
            );
        }
    }

    /**
     * Marks a pending payment as paid when the order is delivered.
     *
     * @param order delivered order
     */
    public void markPaymentAsPaid(Order order) {

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Payment not found"
                        ));

        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new InformationExistsException(
                    "Payment cannot be marked as paid"
            );
        }

        payment.setStatus(PaymentStatus.PAID);

        paymentRepository.save(payment);
    }

    /**
     * Cancels a pending payment when its order is cancelled.
     *
     * @param order cancelled order
     */
    public void cancelPayment(Order order) {

        Optional<Payment> optionalPayment =
                paymentRepository.findByOrder(order);

        if (optionalPayment.isEmpty()) {
            return;
        }

        Payment payment = optionalPayment.get();

        if (payment.getStatus() == PaymentStatus.PENDING) {

            payment.setStatus(PaymentStatus.CANCELLED);

            paymentRepository.save(payment);
        }
    }

    /**
     * Converts a payment entity into an API response.
     *
     * @param payment payment entity
     * @return payment response
     */
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