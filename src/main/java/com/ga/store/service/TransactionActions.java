package com.ga.store.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Coordinates external actions with database transactions.
 * Supports actions after a successful commit and cleanup
 * actions after a rollback.
 */
public final class TransactionActions {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionActions.class);

    private TransactionActions() {
    }

    /**
     * Runs an action after the current database transaction commits.
     * If no transaction is active, the action runs immediately.
     * Action failures are logged without changing the committed result.
     *
     * @param action external action to perform
     */
    public static void afterCommit(Runnable action) {

        if (!TransactionSynchronizationManager
                .isActualTransactionActive()) {

            runSafely(action);
            return;
        }

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCommit() {
                                runSafely(action);
                            }
                        }
                );
    }

    /**
     * Registers a cleanup action for a transaction rollback.
     * No action is registered when no transaction is active.
     *
     * @param action cleanup action to perform after rollback
     */
    public static void afterRollback(Runnable action) {

        if (!TransactionSynchronizationManager
                .isActualTransactionActive()) {

            return;
        }

        TransactionSynchronizationManager
                .registerSynchronization(
                        new TransactionSynchronization() {

                            @Override
                            public void afterCompletion(int status) {

                                if (status == STATUS_ROLLED_BACK) {
                                    runSafely(action);
                                }
                            }
                        }
                );
    }

    /**
     * Runs an external action and logs failures.
     *
     * @param action action to perform
     */
    private static void runSafely(Runnable action) {

        try {

            action.run();

        } catch (RuntimeException exception) {

            logger.error(
                    "External action failed; the database result is unchanged",
                    exception
            );
        }
    }
}