package com.ga.store.repository;

import com.ga.store.model.Order;
import com.ga.store.model.Payment;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    List<Payment> findByUserOrderByCreatedAtDesc(
            User user
    );

    boolean existsByOrder(Order order);
}