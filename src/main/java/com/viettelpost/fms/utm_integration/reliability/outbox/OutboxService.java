package com.viettelpost.fms.utm_integration.reliability.outbox;

public interface OutboxService {

    void enqueue(String eventType, String aggregateType, String aggregateId, String payload);
}