package com.ga.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configures background execution for email delivery.
 */
@Configuration
@EnableAsync
public class EmailDeliveryConfig {

    /**
     * Creates a bounded executor for email delivery tasks.
     * If the queue is full, the calling thread performs the task.
     * During shutdown, queued tasks are allowed time to complete.
     *
     * @return email delivery executor
     */
    @Bean("emailExecutor")
    public ThreadPoolTaskExecutor emailExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("email-");

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }
}