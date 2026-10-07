package com.ga.store.repository;

import com.ga.store.model.PasswordResetToken;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Provides database access for password reset tokens.
 */
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Finds the account email without loading the token entity.
     * This allows the account to be locked before loading and
     * validating the token.
     *
     * @param token password reset token
     * @return associated account email if present
     */
    @Query(
            "SELECT t.user.email FROM PasswordResetToken t "
                    + "WHERE t.token = :token"
    )
    Optional<String> findEmailByToken(
            @Param("token") String token
    );
}