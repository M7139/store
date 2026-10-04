package com.ga.store.service;

import com.ga.store.model.Cart;
import com.ga.store.model.User;
import com.ga.store.repository.CartRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;

    public CartService(
            CartRepository cartRepository,
            UserService userService) {

        this.cartRepository = cartRepository;
        this.userService = userService;
    }

    public Cart getOrCreateCart(String email) {

        User user = userService.getUserByEmail(email);

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart(user);
                    return cartRepository.save(cart);
                });
    }
}