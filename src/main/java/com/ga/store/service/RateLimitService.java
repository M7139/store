package com.ga.store.service;

import com.ga.store.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides simple in-memory request rate limiting.
 */
@Service
public class RateLimitService {

    private final Map<String, RateLimitEntry> attempts =
            new ConcurrentHashMap<>();

    /**
     * Checks whether a request key has exceeded its allowed
     * number of requests within a time window.
     *
     * @param key unique rate limit key
     * @param maxAttempts maximum requests allowed
     * @param windowMinutes time window in minutes
     * @throws RateLimitExceededException when the limit is exceeded
     */
    public void checkLimit(
            String key,
            int maxAttempts,
            int windowMinutes) {

        LocalDateTime now =
                LocalDateTime.now();

        RateLimitEntry entry =
                attempts.get(key);

        if (entry == null
                || now.isAfter(entry.windowStart
                .plusMinutes(windowMinutes))) {

            attempts.put(
                    key,
                    new RateLimitEntry(
                            1,
                            now
                    )
            );

            return;
        }

        if (entry.attemptCount
                >= maxAttempts) {

            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }

        entry.attemptCount++;
    }

    /**
     * Stores the number of requests and the beginning
     * of the current rate limit window.
     */
    private static class RateLimitEntry {

        private int attemptCount;
        private final LocalDateTime windowStart;

        private RateLimitEntry(
                int attemptCount,
                LocalDateTime windowStart) {

            this.attemptCount =
                    attemptCount;

            this.windowStart =
                    windowStart;
        }
    }
}