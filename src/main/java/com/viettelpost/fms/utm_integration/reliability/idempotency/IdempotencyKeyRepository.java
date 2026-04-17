package com.viettelpost.fms.utm_integration.reliability.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {

    Optional<IdempotencyKeyEntity> findByOperationAndKeyValue(String operation, String keyValue);
}
