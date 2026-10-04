package com.ga.store.repository;

import com.ga.store.model.Cart;
import com.ga.store.model.CartItem;
import com.ga.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(
            Cart cart,
            Product product
    );

    void deleteByCart(Cart cart);
}