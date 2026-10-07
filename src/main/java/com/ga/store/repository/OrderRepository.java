package com.ga.store.repository;

import com.ga.store.model.Order;
import com.ga.store.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Provides database access for customer orders.
 */
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Finds and locks an order for an update.
     * Must be called inside an active transaction.
     *
     * @param id order ID
     * @return matching order if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(
            @Param("id") Long id
    );
}