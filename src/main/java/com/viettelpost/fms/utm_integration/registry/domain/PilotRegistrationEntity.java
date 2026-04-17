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
@Table(name = "pilot_registration")
public class PilotRegistrationEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "personal_id_number", nullable = false, length = 100, unique = true)
    private String personalIdNumber;

    @Column(name = "utm_pilot_id", length = 100)
    private String utmPilotId;

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

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "legit_verify", length = 100)
    private String legitVerify;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "organization_id", length = 100)
    private String organizationId;

    @Column(name = "last_synced_at")
    private Date lastSyncedAt;

    @Column(name = "submitted_session_id", length = 100)
    private String submittedSessionId;
}
