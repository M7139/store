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

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordResetToken createPasswordResetToken(User user) {

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