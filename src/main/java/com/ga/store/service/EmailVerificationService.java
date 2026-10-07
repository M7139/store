package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.exception.VerificationTokenExpiredException;
import com.ga.store.model.EmailVerificationToken;
import com.ga.store.model.User;
import com.ga.store.repository.EmailVerificationTokenRepository;
import com.ga.store.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles email verification tokens and account verification.
 */
@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EmailVerificationService(
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            EmailService emailService) {

        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Creates a verification token that expires after 24 hours
     * and sends it to the user.
     *
     * @param user user requiring verification
     * @return saved verification token
     */
    public EmailVerificationToken createVerificationToken(User user) {

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        EmailVerificationToken verificationToken =
                new EmailVerificationToken(
                        token,
                        user,
                        expiresAt
                );

        EmailVerificationToken savedToken =
                emailVerificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(
                user.getEmail(),
                savedToken.getToken()
        );

        return savedToken;
    }

    /**
     * Verifies a user's email using a valid verification token.
     *
     * @param token verification token
     */
    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Verification token not found"
                                ));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException(
                    "Verification token has expired"
            );
        }

        User user = verificationToken.getUser();

        user.setVerified(true);

        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }
}