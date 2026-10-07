package com.ga.store.repository;

import com.ga.store.model.EmailVerificationToken;
import com.ga.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Provides database access for email verification tokens.
 */
public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUser(User user);

    /**
     * Finds the account email without loading the token entity.
     * The account can then be locked before validating the token.
     *
     * @param token verification token
     * @return associated account email if present
     */
    @Query(
            "SELECT t.user.email FROM EmailVerificationToken t "
                    + "WHERE t.token = :token"
    )
    Optional<String> findEmailByToken(
            @Param("token") String token
    );
}