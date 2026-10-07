package com.ga.store.repository;

import com.ga.store.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Provides database access for user accounts.
 */
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Finds and locks a user by email.
     * Must be called within a transaction.
     *
     * @param email user email
     * @return matching user if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailForUpdate(
            @Param("email") String email
    );

    /**
     * Finds and locks a user by ID.
     * Must be called within a transaction.
     *
     * @param id user ID
     * @return matching user if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(
            @Param("id") Long id
    );
}