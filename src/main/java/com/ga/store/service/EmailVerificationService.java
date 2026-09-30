package com.ga.store.service;

import com.ga.store.exception.InformationNotFoundException;
import com.ga.store.model.EmailVerificationToken;
import com.ga.store.model.User;
import com.ga.store.repository.EmailVerificationTokenRepository;
import com.ga.store.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository) {

        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
    }

    public EmailVerificationToken createVerificationToken(User user) {

        String token = UUID.randomUUID().toString();

        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        EmailVerificationToken verificationToken =
                new EmailVerificationToken(
                        token,
                        user,
                        expiresAt
                );

        return emailVerificationTokenRepository.save(verificationToken);
    }

    public void verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                emailVerificationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Verification token not found"
                                ));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        User user = verificationToken.getUser();

        user.setVerified(true);

        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }
}