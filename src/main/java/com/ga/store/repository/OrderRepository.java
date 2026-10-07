package com.ga.store.repository;

import com.ga.store.model.Order;
import com.ga.store.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface OrderRepository
        extends JpaRepository<Order, Long> {


    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findById(Long id);


    List<Order> findByUserOrderByCreatedAtDesc(User user);
}