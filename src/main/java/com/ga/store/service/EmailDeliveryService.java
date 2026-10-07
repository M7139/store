package com.ga.store.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Delivers emails in the background and retries failed delivery attempts.
 */
@Service
public class EmailDeliveryService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailDeliveryService.class);

    private static final int MAX_ATTEMPTS = 3;

    private final JavaMailSender mailSender;

    public EmailDeliveryService(
            JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    /**
     * Sends an email using the configured background executor.
     * Failed delivery is retried up to three total attempts,
     * with a short delay between attempts.
     * Email bodies are not logged because they may contain
     * verification or password reset tokens.
     *
     * @param message email to deliver
     */
    @Async("emailExecutor")
    public void send(SimpleMailMessage message) {

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            try {

                mailSender.send(message);
                return;

            } catch (MailException exception) {

                if (attempt == MAX_ATTEMPTS) {

                    logger.error(
                            "Email delivery failed after {} attempts ({})",
                            MAX_ATTEMPTS,
                            exception.getClass().getSimpleName()
                    );

                    return;
                }

                logger.warn(
                        "Email delivery attempt {} failed; retrying ({})",
                        attempt,
                        exception.getClass().getSimpleName()
                );

                try {

                    Thread.sleep(attempt * 1000L);

                } catch (InterruptedException interrupted) {

                    Thread.currentThread().interrupt();

                    logger.warn(
                            "Email delivery retry interrupted"
                    );

                    return;
                }
            }
        }
    }
}