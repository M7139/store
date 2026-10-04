package com.ga.store.service;

import com.ga.store.dto.CartItemRequest;
import com.ga.store.exception.InformationExistsException;
import com.ga.store.model.Cart;
import com.ga.store.model.CartItem;
import com.ga.store.model.Product;
import com.ga.store.model.User;
import com.ga.store.repository.CartItemRepository;
import com.ga.store.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserService userService,
            ProductService productService) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.productService = productService;
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
}