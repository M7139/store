package com.ga.store.service;

import com.ga.store.dto.CartItemQuantityRequest;
import com.ga.store.dto.CartItemRequest;
import com.ga.store.dto.CartItemResponse;
import com.ga.store.dto.CartResponse;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.Cart;
import com.ga.store.model.CartItem;
import com.ga.store.model.Product;
import com.ga.store.model.User;
import com.ga.store.repository.CartItemRepository;
import com.ga.store.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;
    private final ProductImageService productImageService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserService userService,
            ProductService productService,
            ProductImageService productImageService) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
        this.productImageService = productImageService;
    }

    public Cart getOrCreateCart(String email) {

        User user = userService.getUserByEmail(email);

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }

    public CartItem addItemToCart(
            String email,
            CartItemRequest request) {

        Cart cart = getOrCreateCart(email);

        Product product =
                productService.getActiveProductById(
                        request.getProductId()
                );

        Optional<CartItem> existingCartItem =
                cartItemRepository.findByCartAndProduct(
                        cart,
                        product
                );

        int newQuantity = request.getQuantity();

        if (existingCartItem.isPresent()) {

            newQuantity =
                    existingCartItem.get().getQuantity()
                            + request.getQuantity();
        }

        if (newQuantity > product.getStockQuantity()) {

            throw new InformationExistsException(
                    "Requested quantity exceeds available stock"
            );
        }

        if (existingCartItem.isPresent()) {

            CartItem cartItem =
                    existingCartItem.get();

            cartItem.setQuantity(newQuantity);

            return cartItemRepository.save(cartItem);
        }

        CartItem cartItem = new CartItem(
                cart,
                product,
                request.getQuantity()
        );

        return cartItemRepository.save(cartItem);
    }

    public CartResponse getCart(String email) {

        Cart cart = getOrCreateCart(email);

        List<CartItem> cartItems =
                cartItemRepository.findByCart(cart);

        List<CartItemResponse> itemResponses =
                cartItems.stream()
                        .map(this::createCartItemResponse)
                        .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return new CartResponse(
                cart.getId(),
                itemResponses,
                total
        );
    }

    public CartItem updateCartItemQuantity(
            String email,
            Long cartItemId,
            CartItemQuantityRequest request) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Cart item not found"
                                ));

        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {

            throw new InformationNotFoundException(
                    "Cart item not found"
            );
        }

        Product product =
                productService.getActiveProductById(
                        cartItem.getProduct().getId()
                );

        if (request.getQuantity()
                > product.getStockQuantity()) {

            throw new InformationExistsException(
                    "Requested quantity exceeds available stock"
            );
        }

        cartItem.setQuantity(
                request.getQuantity()
        );

        return cartItemRepository.save(cartItem);
    }

    public void removeCartItem(
            String email,
            Long cartItemId) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository.findById(cartItemId)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Cart item not found"
                                ));

        if (!cartItem.getCart().getId()
                .equals(cart.getId())) {

            throw new InformationNotFoundException(
                    "Cart item not found"
            );
        }

        cartItemRepository.delete(cartItem);
    }

    private CartItemResponse createCartItemResponse(
            CartItem cartItem) {

        Product product = cartItem.getProduct();

        BigDecimal subtotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

        String primaryImageUrl =
                productImageService.getPrimaryImageUrl(
                        product.getId()
                );

        return new CartItemResponse(
                cartItem.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                cartItem.getQuantity(),
                subtotal,
                primaryImageUrl
        );
    }
}