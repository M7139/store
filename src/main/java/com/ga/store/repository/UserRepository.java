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

    /**
     * Finds a user by email without case sensitivity.
     *
     * @param email user email
     * @return matching user if present
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.email) = LOWER(:email)
            """)
    Optional<User> findByEmail(
            @Param("email") String email
    );

    /**
     * Checks whether an email already exists without
     * case sensitivity.
     *
     * @param email email to check
     * @return true if the email exists
     */
    @Query("""
            SELECT CASE
                WHEN COUNT(u) > 0 THEN true
                ELSE false
            END
            FROM User u
            WHERE LOWER(u.email) = LOWER(:email)
            """)
    boolean existsByEmail(
            @Param("email") String email
    );

    /**
     * Finds and locks a user by email without case sensitivity.
     * Must be called within a transaction.
     *
     * @param email user email
     * @return matching user if present
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.email) = LOWER(:email)
            """)
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
    @Query("""
            SELECT u
            FROM User u
            WHERE u.id = :id
            """)
    Optional<User> findByIdForUpdate(
            @Param("id") Long id
    );
}