package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.exception.VerificationTokenExpiredException;
import com.ga.store.model.PasswordResetToken;
import com.ga.store.model.User;
import com.ga.store.repository.PasswordResetTokenRepository;
import com.ga.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles password reset token creation, email delivery
 * and password replacement.
 * Password replacement, JWT invalidation and reset token removal
 * happen within one database transaction.
 */
@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    /**
     * Creates a password reset token and schedules email delivery
     * after the database transaction commits.
     *
     * @param email account email
     * @return created reset token
     */
    @Transactional
    public PasswordResetToken requestPasswordReset(String email) {

        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));

        PasswordResetToken resetToken =
                createPasswordResetToken(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetToken.getToken()
        );

        return resetToken;
    }

    /**
     * Creates a one-hour password reset token.
     * If a token record already exists, its value and expiration
     * are replaced so the previous reset link no longer works.
     *
     * @param user user requesting reset
     * @return created or updated reset token
     */
    @Transactional
    public PasswordResetToken createPasswordResetToken(User user) {

        user = userRepository.findByEmailForUpdate(
                user.getEmail()
        ).orElseThrow(() ->
                new InformationNotFoundException(
                        "User with this email not found"
                ));

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByUser(user)
                        .orElse(null);

        if (resetToken == null) {

            resetToken = new PasswordResetToken(
                    token,
                    user,
                    expiresAt
            );

        } else {

            resetToken.setToken(token);
            resetToken.setExpiresAt(expiresAt);
        }

        return passwordResetTokenRepository.save(resetToken);
    }

    /**
     * Replaces a user's password using a valid reset token.
     * The account is locked before validating the token.
     * Increasing the token version invalidates existing JWTs.
     * The reset token is deleted in the same transaction.
     *
     * @param token password reset token
     * @param newPassword new password
     */
    @Transactional
    public void resetPassword(
            String token,
            String newPassword) {

        String email = passwordResetTokenRepository
                .findEmailByToken(token)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Password reset token not found"
                        ));

        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Password reset token not found"
                                ));

        if (!resetToken.getExpiresAt().isAfter(
                LocalDateTime.now())) {

            throw new VerificationTokenExpiredException(
                    "Password reset token has expired"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(newPassword);

        user.setPasswordHash(hashedPassword);

        user.setTokenVersion(
                user.getTokenVersion() + 1
        );

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}