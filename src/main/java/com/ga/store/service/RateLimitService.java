package com.ga.store.service;

import com.ga.store.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, RateLimitEntry> attempts =
            new ConcurrentHashMap<>();

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