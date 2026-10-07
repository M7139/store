package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.exception.VerificationTokenExpiredException;
import com.ga.store.model.PasswordResetToken;
import com.ga.store.model.User;
import com.ga.store.repository.PasswordResetTokenRepository;
import com.ga.store.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles password reset token creation, email delivery
 * and password replacement.
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
     * Creates and emails a password reset token for a user.
     *
     * @param email account email
     * @return created reset token
     */
    public PasswordResetToken requestPasswordReset(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));

        PasswordResetToken resetToken = createPasswordResetToken(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetToken.getToken()
        );

        return resetToken;
    }

    /**
     * Creates a one-hour password reset token.
     * Any previous token for the user is removed.
     *
     * @param user user requesting reset
     * @return created reset token
     */
    public PasswordResetToken createPasswordResetToken(User user) {

        passwordResetTokenRepository.findByUser(user)
                .ifPresent(passwordResetTokenRepository::delete);

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken =
                new PasswordResetToken(
                        token,
                        user,
                        expiresAt
                );

        return passwordResetTokenRepository.save(resetToken);
    }

    /**
     * Replaces a user's password using a valid reset token.
     *
     * @param token password reset token
     * @param newPassword new password
     */
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken =
                passwordResetTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Password reset token not found"
                                ));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException(
                    "Password reset token has expired"
            );
        }

        User user = resetToken.getUser();

        String hashedPassword = passwordEncoder.encode(newPassword);

        user.setPasswordHash(hashedPassword);

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}