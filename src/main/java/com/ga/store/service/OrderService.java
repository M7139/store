package com.ga.store.service;

import com.ga.store.dto.OrderItemResponse;
import com.ga.store.dto.OrderResponse;
import com.ga.store.enums.OrderStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.*;
import com.ga.store.repository.CartItemRepository;
import com.ga.store.repository.OrderItemRepository;
import com.ga.store.repository.OrderRepository;
import com.ga.store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final AddressService addressService;
    private final CartService cartService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserService userService,
            AddressService addressService,
            CartService cartService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
    }

    @Transactional
    public OrderResponse checkout(String email) {

        User user =
                userService.getUserByEmail(email);

        Address address =
                addressService.getAddressByUser(email);

        Cart cart =
                cartService.getOrCreateCart(email);

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new InformationExistsException(
                    "Cart is empty"
            );
        }

        BigDecimal totalAmount =
                BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            if (!product.isActive()) {
                throw new InformationExistsException(
                        product.getName()
                                + " is no longer available"
                );
            }

            if (cartItem.getQuantity()
                    > product.getStockQuantity()) {

                throw new InformationExistsException(
                        "Not enough stock for "
                                + product.getName()
                );
            }

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            totalAmount =
                    totalAmount.add(subtotal);
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

        Order savedOrder =
                orderRepository.save(order);

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            OrderItem orderItem =
                    new OrderItem(
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

    public List<OrderResponse> getOrdersByUser(
            String email) {

        User user =
                userService.getUserByEmail(email);

        List<Order> orders =
                orderRepository
                        .findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::createOrderResponse)
                .toList();
    }

    public OrderResponse getOrderByIdForUser(
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

        return createOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(
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

        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {

            throw new InformationExistsException(
                    "Order cannot be cancelled"
            );
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder(order);

        for (OrderItem orderItem : orderItems) {

            Product product =
                    orderItem.getProduct();

            product.setStockQuantity(
                    product.getStockQuantity()
                            + orderItem.getQuantity()
            );

            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order savedOrder =
                orderRepository.save(order);

        return createOrderResponse(savedOrder);
    }

    private OrderResponse createOrderResponse(
            Order order) {

        List<OrderItemResponse> itemResponses =
                orderItemRepository
                        .findByOrder(order)
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