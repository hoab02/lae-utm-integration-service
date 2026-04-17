package com.viettelpost.fms.utm_integration.registry.domain;

import com.viettelpost.fms.utm_integration.domain.Auditor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "drone_registration")
public class DroneRegistrationEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "serial_number", nullable = false, length = 100, unique = true)
    private String serialNumber;

    @Column(name = "utm_drone_id", length = 100)
    private String utmDroneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private RegistrationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", length = 50)
    private RegistrationSyncStatus syncStatus;

    @Column(name = "submitted_at")
    private Date submittedAt;

    @Column(name = "approved_at")
    private Date approvedAt;

    @Column(name = "rejected_at")
    private Date rejectedAt;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "sync_error_code", length = 100)
    private String syncErrorCode;

    @Column(name = "sync_error_message", length = 1000)
    private String syncErrorMessage;

    @Column(name = "request_payload_snapshot", columnDefinition = "TEXT")
    private String requestPayloadSnapshot;

    @Column(name = "response_payload_snapshot", columnDefinition = "TEXT")
    private String responsePayloadSnapshot;

    @Column(name = "registration_id", length = 100)
    private String registrationId;

    @Column(name = "drone_status")
    private Integer droneStatus;

    @Column(name = "status_legit")
    private Integer statusLegit;

    @Column(name = "status_legit_note", length = 1000)
    private String statusLegitNote;

    @Column(name = "last_synced_at")
    private Date lastSyncedAt;

    @Column(name = "submitted_session_id", length = 100)
    private String submittedSessionId;
}