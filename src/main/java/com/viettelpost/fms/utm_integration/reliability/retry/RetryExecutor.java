package com.viettelpost.fms.utm_integration.reliability.retry;

import org.springframework.stereotype.Component;

@Component
public class RetryExecutor {

    private static final int MAX_ATTEMPTS = 3;

    public <T> T execute(RetryableAction<T> action) {
        RuntimeException lastException = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return action.execute();
            } catch (RuntimeException ex) {
                lastException = ex;
                if (attempt == MAX_ATTEMPTS) {
                    throw ex;
                }
            }
        }
        throw lastException;
    }

    @FunctionalInterface
    public interface RetryableAction<T> {
        T execute();
    }
}
