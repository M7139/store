package com.ga.store.service;

import com.ga.store.dto.OrderItemResponse;
import com.ga.store.dto.OrderResponse;
import com.ga.store.dto.OrderStatusRequest;
import com.ga.store.enums.OrderStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.*;
import com.ga.store.repository.CartItemRepository;
import com.ga.store.repository.OrderItemRepository;
import com.ga.store.repository.OrderRepository;
import com.ga.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles checkout, order history, cancellation, stock allocation
 * and the order status workflow.
 */
@Service
public class OrderService {

    private static final Logger logger =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final OrderNotificationService orderNotificationService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserService userService,
            AddressService addressService,
            CartService cartService,
            PaymentService paymentService,
            EmailService emailService,
            OrderNotificationService orderNotificationService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.orderNotificationService = orderNotificationService;
    }

    /**
     * Converts the user's cart into an order.
     * Products are locked during checkout to prevent concurrent
     * orders from overselling stock.
     *
     * @param email authenticated customer's email
     * @return created order response
     */
    @Transactional
    public OrderResponse checkout(String email) {

        User user = userService.getUserByEmail(email);

        Address address =
                addressService.getAddressByUser(email);

        Cart cart = cartService.getOrCreateCart(email);

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {

            logger.warn(
                    "Checkout failed for user id {} because cart is empty",
                    user.getId()
            );

            throw new InformationExistsException(
                    "Cart is empty"
            );
        }

        cartItems.sort(
                (firstItem, secondItem) ->
                        firstItem.getProduct()
                                .getId()
                                .compareTo(
                                        secondItem.getProduct().getId()
                                )
        );

        Map<Long, Product> lockedProducts =
                new HashMap<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Product product = productRepository
                    .findByIdForUpdate(
                            cartItem.getProduct().getId()
                    )
                    .orElseThrow(() ->
                            new InformationNotFoundException(
                                    "Product not found"
                            ));

            lockedProducts.put(
                    product.getId(),
                    product
            );

            if (!product.isActive()) {

                logger.warn(
                        "Checkout failed for user id {} because product id {} is inactive",
                        user.getId(),
                        product.getId()
                );

                throw new InformationExistsException(
                        product.getName()
                                + " is no longer available"
                );
            }

            if (cartItem.getQuantity()
                    > product.getStockQuantity()) {

                logger.warn(
                        "Checkout failed for user id {} because product id {} has insufficient stock",
                        user.getId(),
                        product.getId()
                );

                throw new InformationExistsException(
                        "Not enough stock for "
                                + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            totalAmount = totalAmount.add(subtotal);
        }

        Order order = new Order(
                user,
                totalAmount,
                address.getHouse(),
                address.getRoad(),
                address.getBlock(),
                address.getArea(),
                address.getPhoneNumber()
        );

        Order savedOrder = orderRepository.save(order);

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product = lockedProducts.get(
                    cartItem.getProduct().getId()
            );

            BigDecimal subtotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    product.getName(),
                    product.getPrice(),
                    cartItem.getQuantity(),
                    subtotal
            );

            OrderItem savedOrderItem =
                    orderItemRepository.save(orderItem);

            itemResponses.add(
                    new OrderItemResponse(
                            savedOrderItem.getId(),
                            product.getId(),
                            savedOrderItem.getProductName(),
                            savedOrderItem.getPrice(),
                            savedOrderItem.getQuantity(),
                            savedOrderItem.getSubtotal()
                    )
            );

            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        cartItemRepository.deleteAll(cartItems);

        logger.info(
                "Order id {} created successfully for user id {} with total {}",
                savedOrder.getId(),
                user.getId(),
                savedOrder.getTotalAmount()
        );

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                savedOrder.getHouse(),
                savedOrder.getRoad(),
                savedOrder.getBlock(),
                savedOrder.getArea(),
                savedOrder.getPhoneNumber(),
                itemResponses,
                savedOrder.getCreatedAt()
        );
    }

    /**
     * Returns all orders belonging to a customer.
     *
     * @param email authenticated customer's email
     * @return customer's orders
     */
    public List<OrderResponse> getOrdersByUser(
            String email) {

        User user = userService.getUserByEmail(email);

        List<Order> orders = orderRepository
                .findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::createOrderResponse)
                .toList();
    }

    /**
     * Returns an order only if it belongs to the authenticated customer.
     *
     * @param email authenticated customer's email
     * @param orderId order ID
     * @return matching order
     */
    public OrderResponse getOrderByIdForUser(
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

        return createOrderResponse(order);
    }

    /**
     * Cancels a customer's pending or confirmed order
     * and restores its allocated stock.
     * The order remains locked until the transaction completes.
     *
     * @param email authenticated customer's email
     * @param orderId order ID
     * @return cancelled order
     */
    @Transactional
    public OrderResponse cancelOrder(
            String email,
            Long orderId) {

        User user = userService.getUserByEmail(email);

        Order order = orderRepository
                .findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Order not found"
                        ));

        if (!order.getUser().getId().equals(user.getId())) {

            logger.warn(
                    "User id {} attempted to cancel order id {} owned by another user",
                    user.getId(),
                    orderId
            );

            throw new InformationNotFoundException(
                    "Order not found"
            );
        }

        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {

            logger.warn(
                    "Cancellation rejected for order id {} with status {}",
                    order.getId(),
                    order.getStatus()
            );

            throw new InformationExistsException(
                    "Order cannot be cancelled"
            );
        }

        restoreOrderStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        Order savedOrder = orderRepository.save(order);

        paymentService.cancelPayment(savedOrder);

        emailService.sendOrderCancelledEmail(
                savedOrder.getUser().getEmail(),
                savedOrder.getId()
        );

        orderNotificationService.sendOrderStatusUpdate(
                savedOrder.getUser().getEmail(),
                savedOrder.getId(),
                savedOrder.getStatus()
        );

        logger.info(
                "Order id {} cancelled by user id {}",
                savedOrder.getId(),
                user.getId()
        );

        return createOrderResponse(savedOrder);
    }

    /**
     * Returns all orders for administrator use.
     *
     * @return all orders
     */
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::createOrderResponse)
                .toList();
    }

    /**
     * Changes an order's status according to the permitted workflow.
     * A pending payment must exist before the order can be confirmed.
     * The order remains locked until the transaction completes.
     * Appropriate email, payment and real-time notification actions
     * are also performed.
     *
     * @param orderId order ID
     * @param request requested status
     * @return updated order
     */
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusRequest request) {

        Order order = orderRepository
                .findByIdForUpdate(orderId)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Order not found"
                        ));

        OrderStatus currentStatus = order.getStatus();

        OrderStatus newStatus = request.getStatus();

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        if (newStatus == OrderStatus.CONFIRMED) {

            paymentService.requirePendingPayment(order);
        }

        if (newStatus == OrderStatus.CANCELLED) {

            restoreOrderStock(order);
        }

        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.CONFIRMED) {

            emailService.sendOrderConfirmedEmail(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getId()
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {

            paymentService.cancelPayment(savedOrder);

            emailService.sendOrderCancelledEmail(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getId()
            );
        }

        if (newStatus == OrderStatus.DELIVERED) {

            paymentService.markPaymentAsPaid(savedOrder);

            emailService.sendOrderDeliveredEmail(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getId()
            );
        }

        orderNotificationService.sendOrderStatusUpdate(
                savedOrder.getUser().getEmail(),
                savedOrder.getId(),
                savedOrder.getStatus()
        );

        logger.info(
                "Order id {} status changed from {} to {}",
                savedOrder.getId(),
                currentStatus,
                newStatus
        );

        return createOrderResponse(savedOrder);
    }

    /**
     * Validates whether an order status transition is allowed.
     *
     * @param currentStatus existing status
     * @param newStatus requested status
     */
    private void validateStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        boolean validTransition = switch (currentStatus) {

            case PENDING ->
                    newStatus == OrderStatus.CONFIRMED
                            || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == OrderStatus.PROCESSING
                            || newStatus == OrderStatus.CANCELLED;

            case PROCESSING ->
                    newStatus == OrderStatus.SHIPPED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };

        if (!validTransition) {

            logger.warn(
                    "Invalid order status transition from {} to {}",
                    currentStatus,
                    newStatus
            );

            throw new InformationExistsException(
                    "Invalid order status change from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    /**
     * Returns quantities from a cancelled order back to product stock.
     *
     * @param order cancelled order
     */
    private void restoreOrderStock(Order order) {

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        for (OrderItem orderItem : orderItems) {

            Product product = productRepository
                    .findByIdForUpdate(
                            orderItem.getProduct().getId()
                    )
                    .orElseThrow(() ->
                            new InformationNotFoundException(
                                    "Product not found"
                            ));

            product.setStockQuantity(
                    product.getStockQuantity()
                            + orderItem.getQuantity()
            );

            productRepository.save(product);
        }

        logger.info(
                "Stock restored for cancelled order id {}",
                order.getId()
        );
    }

    /**
     * Converts an order entity into an API response.
     *
     * @param order order entity
     * @return order response
     */
    private OrderResponse createOrderResponse(Order order) {

        List<OrderItemResponse> itemResponses =
                orderItemRepository.findByOrder(order)
                        .stream()
                        .map(orderItem ->
                                new OrderItemResponse(
                                        orderItem.getId(),
                                        orderItem.getProduct().getId(),
                                        orderItem.getProductName(),
                                        orderItem.getPrice(),
                                        orderItem.getQuantity(),
                                        orderItem.getSubtotal()
                                ))
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getHouse(),
                order.getRoad(),
                order.getBlock(),
                order.getArea(),
                order.getPhoneNumber(),
                itemResponses,
                order.getCreatedAt()
        );
    }
}