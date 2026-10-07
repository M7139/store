package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.exception.VerificationTokenExpiredException;
import com.ga.store.model.EmailVerificationToken;
import com.ga.store.model.User;
import com.ga.store.repository.EmailVerificationTokenRepository;
import com.ga.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles email verification tokens and account verification.
 * Verification and token updates are performed transactionally.
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
     * Creates a verification token that expires after 24 hours.
     * An existing token record is updated instead of creating
     * another record for the same user.
     * Email delivery begins after the transaction commits.
     *
     * @param user user requiring verification
     * @return saved verification token
     */
    @Transactional
    public EmailVerificationToken createVerificationToken(User user) {

        user = userRepository.findByEmailForUpdate(
                user.getEmail()
        ).orElseThrow(() ->
                new InformationNotFoundException(
                        "User with this email not found"
                ));

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt =
                LocalDateTime.now().plusHours(24);

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByUser(user)
                        .orElse(null);

        if (verificationToken == null) {

            verificationToken = new EmailVerificationToken(
                    token,
                    user,
                    expiresAt
            );

        } else {

            verificationToken.setToken(token);
            verificationToken.setExpiresAt(expiresAt);
        }

        EmailVerificationToken savedToken =
                emailVerificationTokenRepository.save(
                        verificationToken
                );

        emailService.sendVerificationEmail(
                user.getEmail(),
                savedToken.getToken()
        );

        return savedToken;
    }

    /**
     * Sends a replacement verification email for an unverified account.
     * Missing and already verified accounts do not trigger an email.
     * Replacing the token invalidates the previous verification link.
     *
     * @param email account email
     */
    @Transactional
    public void resendVerification(String email) {

        userRepository.findByEmailForUpdate(email)
                .filter(user -> !user.isVerified())
                .ifPresent(this::createVerificationToken);
    }

    /**
     * Verifies a user's email using a valid verification token.
     * The account is locked before validating the token.
     * Account verification and token deletion happen in one transaction.
     *
     * @param token verification token
     */
    @Transactional
    public void verifyEmail(String token) {

        String email = emailVerificationTokenRepository
                .findEmailByToken(token)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "Verification token not found"
                        ));

        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() ->
                        new InformationNotFoundException(
                                "User with this email not found"
                        ));

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Verification token not found"
                                ));

        if (!verificationToken.getExpiresAt().isAfter(
                LocalDateTime.now())) {

            throw new VerificationTokenExpiredException(
                    "Verification token has expired"
            );
        }

        user.setVerified(true);

        userRepository.save(user);

        emailVerificationTokenRepository.delete(
                verificationToken
        );
    }
}