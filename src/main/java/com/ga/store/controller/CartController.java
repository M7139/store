package com.ga.store.controller;

import com.ga.store.dto.CartItemQuantityRequest;
import com.ga.store.dto.CartItemRequest;
import com.ga.store.dto.CartResponse;
import com.ga.store.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(
            CartService cartService) {

        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        String email = authentication.getName();

        CartResponse response =
                cartService.getCart(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        String email = authentication.getName();

        cartService.addItemToCart(
                email,
                request
        );

        CartResponse response =
                cartService.getCart(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateCartItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemQuantityRequest request) {

        String email = authentication.getName();

        cartService.updateCartItemQuantity(
                email,
                cartItemId,
                request
        );

        CartResponse response =
                cartService.getCart(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId) {

        String email = authentication.getName();

        cartService.removeCartItem(
                email,
                cartItemId
        );

        CartResponse response =
                cartService.getCart(email);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @DeleteMapping
    public ResponseEntity<String> clearCart(
            Authentication authentication) {

        String email = authentication.getName();

        cartService.clearCart(email);

        return new ResponseEntity<>(
                "Cart cleared successfully",
                HttpStatus.OK
        );
    }
}