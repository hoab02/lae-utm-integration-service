package com.viettelpost.fms.utm_integration.approval.domain;

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

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "flight_approval")
public class FlightApprovalEntity {

    @Id
    private String id;

    @Column(name = "plan_id", nullable = false, unique = true, length = 100)
    private String planId;

    @Column(name = "mission_id", length = 100)
    private String missionId;

    @Column(name = "utm_application_id", length = 100)
    private String utmApplicationId;

    @Column(name = "utm_request_id", length = 100)
    private String utmRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApprovalStatus status;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "utm_request_payload", columnDefinition = "TEXT")
    private String utmRequestPayload;

    @Column(name = "utm_last_status_payload", columnDefinition = "TEXT")
    private String utmLastStatusPayload;
}