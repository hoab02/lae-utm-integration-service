package com.viettelpost.fms.utm_integration.reliability.idempotency;

import com.viettelpost.fms.utm_integration.domain.Auditor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "idempotency_key", uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotency_operation_key", columnNames = {"operation", "key_value"})
})
public class IdempotencyKeyEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "operation", nullable = false, length = 100)
    private String operation;

    @Column(name = "key_value", nullable = false, length = 200)
    private String keyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private IdempotencyStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    @Column(name = "completed_at")
    private Date completedAt;
}
