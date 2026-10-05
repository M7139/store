package com.ga.store.repository;

import com.ga.store.enums.OrderStatus;
import com.ga.store.model.Order;
import com.ga.store.model.OrderItem;
import com.ga.store.model.Product;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(
            Order order
    );

    boolean existsByOrderUserAndProductAndOrderStatus(
            User user,
            Product product,
            OrderStatus status
    );
}