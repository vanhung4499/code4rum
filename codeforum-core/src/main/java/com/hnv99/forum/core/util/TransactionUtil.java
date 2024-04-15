package com.hnv99.forum.core.util;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Transactional Utility Class
 */
public class TransactionUtil {
    /**
     * Register transaction callback - execute before transaction commit, immediately if not in transaction
     *
     * @param runnable
     */
    public static void registryBeforeCommitOrImmediatelyRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        // In a transaction
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Execute before transaction commit, roll back transaction if error occurs
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    runnable.run();
                }
            });
        } else {
            // Execute immediately
            runnable.run();
        }
    }

    /**
     * Execute after transaction completion/rollback
     *
     * @param runnable
     */
    public static void registryAfterCompletionOrImmediatelyRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        // In a transaction
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Execute after transaction commit or rollback
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    runnable.run();
                }
            });
        } else {
            // Execute immediately
            runnable.run();
        }
    }

    /**
     * Execute after transaction commit
     *
     * @param runnable
     */
    public static void registryAfterCommitOrImmediatelyRun(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        // In a transaction
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            // Execute after transaction commit
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
        } else {
            // Execute immediately
            runnable.run();
        }
    }
}
