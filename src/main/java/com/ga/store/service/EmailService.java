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

    public void sendOrderConfirmedEmail(
            String toEmail,
            Long orderId) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
                "Order Confirmed - #" + orderId
        );

        message.setText(
                "Your order #" + orderId +
                        " has been confirmed.\n\n" +
                        "We will begin preparing your order soon.\n\n" +
                        "Thank you for shopping with us!"
        );

        mailSender.send(message);
    }

    public void sendOrderCancelledEmail(
            String toEmail,
            Long orderId) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
                "Order Cancelled - #" + orderId
        );

        message.setText(
                "Your order #" + orderId +
                        " has been cancelled.\n\n" +
                        "The products have been returned to stock."
        );

        mailSender.send(message);
    }

    public void sendOrderDeliveredEmail(
            String toEmail,
            Long orderId) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
                "Order Delivered - #" + orderId
        );

        message.setText(
                "Your order #" + orderId +
                        " has been delivered.\n\n" +
                        "Thank you for shopping with us!"
        );

        mailSender.send(message);
    }
}