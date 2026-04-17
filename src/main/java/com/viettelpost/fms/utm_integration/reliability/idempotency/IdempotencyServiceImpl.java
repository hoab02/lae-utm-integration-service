package com.viettelpost.fms.utm_integration.reliability.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Override
    @Transactional
    public void begin(String operation, String keyValue) {
        idempotencyKeyRepository.findByOperationAndKeyValue(operation, keyValue)
                .ifPresentOrElse(existing -> {
                    if (IdempotencyStatus.SUCCEEDED.equals(existing.getStatus())) {
                        return;
                    }
                    existing.setStatus(IdempotencyStatus.IN_PROGRESS);
                    existing.setAttemptCount(existing.getAttemptCount() + 1);
                    existing.setLastError(null);
                    existing.setCompletedAt(null);
                    idempotencyKeyRepository.save(existing);
                }, () -> createInProgress(operation, keyValue));
    }

    @Override
    @Transactional
    public void markSucceeded(String operation, String keyValue) {
        IdempotencyKeyEntity entity = idempotencyKeyRepository.findByOperationAndKeyValue(operation, keyValue)
                .orElseGet(() -> IdempotencyKeyEntity.builder()
                        .operation(operation)
                        .keyValue(keyValue)
                        .attemptCount(1)
                        .build());
        entity.setStatus(IdempotencyStatus.SUCCEEDED);
        entity.setLastError(null);
        entity.setCompletedAt(new Date());
        if (entity.getAttemptCount() == 0) {
            entity.setAttemptCount(1);
        }
        idempotencyKeyRepository.save(entity);
    }

    @Override
    @Transactional
    public void markFailed(String operation, String keyValue, String lastError) {
        IdempotencyKeyEntity entity = idempotencyKeyRepository.findByOperationAndKeyValue(operation, keyValue)
                .orElseGet(() -> IdempotencyKeyEntity.builder()
                        .operation(operation)
                        .keyValue(keyValue)
                        .attemptCount(1)
                        .build());
        entity.setStatus(IdempotencyStatus.FAILED);
        entity.setLastError(lastError);
        entity.setCompletedAt(null);
        if (entity.getAttemptCount() == 0) {
            entity.setAttemptCount(1);
        }
        idempotencyKeyRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSucceeded(String operation, String keyValue) {
        return idempotencyKeyRepository.findByOperationAndKeyValue(operation, keyValue)
                .map(entity -> IdempotencyStatus.SUCCEEDED.equals(entity.getStatus()))
                .orElse(false);
    }

    private void createInProgress(String operation, String keyValue) {
        try {
            idempotencyKeyRepository.save(IdempotencyKeyEntity.builder()
                    .operation(operation)
                    .keyValue(keyValue)
                    .status(IdempotencyStatus.IN_PROGRESS)
                    .attemptCount(1)
                    .build());
        } catch (DataIntegrityViolationException ex) {
            idempotencyKeyRepository.findByOperationAndKeyValue(operation, keyValue)
                    .ifPresent(existing -> {
                        existing.setStatus(IdempotencyStatus.IN_PROGRESS);
                        existing.setAttemptCount(existing.getAttemptCount() + 1);
                        existing.setLastError(null);
                        existing.setCompletedAt(null);
                        idempotencyKeyRepository.save(existing);
                    });
        }
    }
}
