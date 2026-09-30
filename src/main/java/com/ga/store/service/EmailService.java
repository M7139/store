package com.ga.store.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(
            String toEmail,
            String verificationToken) {

        String verificationLink =
                "http://localhost:9091/api/auth/verify-email?token="
                        + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your email");

        message.setText(
                "Welcome to our store!\n\n" +
                        "Please verify your email using the link below:\n\n" +
                        verificationLink
        );

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(
            String toEmail,
            String resetToken) {

        String resetLink =
                "http://localhost:9091/reset-password?token="
                        + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset your password");

        message.setText(
                "We received a request to reset your password.\n\n" +
                        "Use the link below to reset your password:\n\n" +
                        resetLink +
                        "\n\nThis link will expire in 1 hour."
        );

        mailSender.send(message);
    }
}