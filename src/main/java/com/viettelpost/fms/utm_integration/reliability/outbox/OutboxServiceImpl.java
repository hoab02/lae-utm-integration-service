package com.viettelpost.fms.utm_integration.reliability.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional
    public void enqueue(String eventType, String aggregateType, String aggregateId, String payload) {
        outboxEventRepository.save(OutboxEventEntity.builder()
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .status(OutboxEventStatus.PENDING)
                .attemptCount(0)
                .build());
    }
}