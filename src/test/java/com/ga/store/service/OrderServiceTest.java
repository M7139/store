package com.ga.store.service;

import com.ga.store.enums.OrderStatus;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.model.Address;
import com.ga.store.model.Cart;
import com.ga.store.model.CartItem;
import com.ga.store.model.Order;
import com.ga.store.model.OrderItem;
import com.ga.store.model.Product;
import com.ga.store.model.User;
import com.ga.store.repository.CartItemRepository;
import com.ga.store.repository.OrderItemRepository;
import com.ga.store.repository.OrderRepository;
import com.ga.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private AddressService addressService;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private EmailService emailService;

    @Mock
    private OrderNotificationService orderNotificationService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {

        orderService = new OrderService(
                orderRepository,
                orderItemRepository,
                cartItemRepository,
                productRepository,
                userService,
                addressService,
                cartService,
                paymentService,
                emailService,
                orderNotificationService
        );
    }

    @Test
    void checkoutShouldFailWhenProductIsInactive() {

        String email =
                "customer@test.com";

        User user =
                new User(
                        "Test",
                        "Customer",
                        email,
                        "password"
                );

        user.setId(1L);

        Address address =
                new Address(
                        user,
                        "10",
                        "20",
                        "30",
                        "Manama",
                        "12345678"
                );

        Cart cart =
                new Cart(user);

        Product product =
                new Product(
                        "Lavender Soap",
                        "Test soap",
                        new BigDecimal("2.50"),
                        10,
                        null
                );

        product.setId(1L);
        product.setActive(false);

        CartItem cartItem =
                new CartItem(
                        cart,
                        product,
                        1
                );

        List<CartItem> cartItems =
                new ArrayList<>();

        cartItems.add(cartItem);

        when(userService.getUserByEmail(
                email
        )).thenReturn(user);

        when(addressService.getAddressByUser(
                email
        )).thenReturn(address);

        when(cartService.getOrCreateCart(
                email
        )).thenReturn(cart);

        when(cartItemRepository.findByCart(
                cart
        )).thenReturn(
                cartItems
        );

        when(productRepository.findByIdForUpdate(
                1L
        )).thenReturn(
                Optional.of(product)
        );

        assertThrows(
                InformationExistsException.class,
                () -> orderService.checkout(
                        email
                )
        );
    }

    @Test
    void checkoutShouldFailWhenStockIsInsufficient() {

        String email =
                "customer@test.com";

        User user =
                new User(
                        "Test",
                        "Customer",
                        email,
                        "password"
                );

        user.setId(1L);

        Address address =
                new Address(
                        user,
                        "10",
                        "20",
                        "30",
                        "Manama",
                        "12345678"
                );

        Cart cart =
                new Cart(user);

        Product product =
                new Product(
                        "Lavender Soap",
                        "Test soap",
                        new BigDecimal("2.50"),
                        2,
                        null
                );

        product.setId(1L);
        product.setActive(true);

        CartItem cartItem =
                new CartItem(
                        cart,
                        product,
                        5
                );

        List<CartItem> cartItems =
                new ArrayList<>();

        cartItems.add(cartItem);

        when(userService.getUserByEmail(
                email
        )).thenReturn(user);

        when(addressService.getAddressByUser(
                email
        )).thenReturn(address);

        when(cartService.getOrCreateCart(
                email
        )).thenReturn(cart);

        when(cartItemRepository.findByCart(
                cart
        )).thenReturn(
                cartItems
        );

        when(productRepository.findByIdForUpdate(
                1L
        )).thenReturn(
                Optional.of(product)
        );

        assertThrows(
                InformationExistsException.class,
                () -> orderService.checkout(
                        email
                )
        );
    }

    @Test
    void cancelOrderShouldCancelPendingOrderAndRestoreStock() {

        String email =
                "customer@test.com";

        Long orderId =
                1L;

        User user =
                new User(
                        "Test",
                        "Customer",
                        email,
                        "password"
                );

        user.setId(1L);

        Product product =
                new Product(
                        "Lavender Soap",
                        "Test soap",
                        new BigDecimal("2.50"),
                        3,
                        null
                );

        product.setId(1L);

        Order order =
                new Order(
                        user,
                        new BigDecimal("5.00"),
                        "10",
                        "20",
                        "30",
                        "Manama",
                        "12345678"
                );

        OrderItem orderItem =
                new OrderItem(
                        order,
                        product,
                        product.getName(),
                        product.getPrice(),
                        2,
                        new BigDecimal("5.00")
                );

        when(userService.getUserByEmail(
                email
        )).thenReturn(user);

        when(orderRepository.findById(
                orderId
        )).thenReturn(
                Optional.of(order)
        );

        when(orderItemRepository.findByOrder(
                order
        )).thenReturn(
                List.of(orderItem)
        );

        when(productRepository.findByIdForUpdate(
                1L
        )).thenReturn(
                Optional.of(product)
        );

        when(orderRepository.save(
                order
        )).thenReturn(order);

        orderService.cancelOrder(
                email,
                orderId
        );

        assertEquals(
                OrderStatus.CANCELLED,
                order.getStatus()
        );

        assertEquals(
                5,
                product.getStockQuantity()
        );
    }

    @Test
    void cancelOrderShouldFailWhenOrderIsProcessing() {

        String email =
                "customer@test.com";

        Long orderId =
                1L;

        User user =
                new User(
                        "Test",
                        "Customer",
                        email,
                        "password"
                );

        user.setId(1L);

        Order order =
                new Order(
                        user,
                        new BigDecimal("5.00"),
                        "10",
                        "20",
                        "30",
                        "Manama",
                        "12345678"
                );

        order.setStatus(
                OrderStatus.PROCESSING
        );

        when(userService.getUserByEmail(
                email
        )).thenReturn(user);

        when(orderRepository.findById(
                orderId
        )).thenReturn(
                Optional.of(order)
        );

        assertThrows(
                InformationExistsException.class,
                () -> orderService.cancelOrder(
                        email,
                        orderId
                )
        );
    }
}