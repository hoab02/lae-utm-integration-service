package com.viettelpost.fms.utm_integration.reliability.idempotency;

public interface IdempotencyService {

    void begin(String operation, String keyValue);

    void markSucceeded(String operation, String keyValue);

    void markFailed(String operation, String keyValue, String lastError);

    boolean hasSucceeded(String operation, String keyValue);
}
