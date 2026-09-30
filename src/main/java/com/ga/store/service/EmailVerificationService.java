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

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository) {

        this.tokenRepository = tokenRepository;
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

        return tokenRepository.save(verificationToken);
    }

    public User verifyEmail(String token) {

        EmailVerificationToken verificationToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new InformationNotFoundException(
                                        "Verification token not found"
                                ));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification token has expired");
        }

        User user = verificationToken.getUser();

        user.setVerified(true);

        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return user;
    }
}