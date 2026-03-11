package com.viettelpost.fms.utm_integration.session.domain;

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
@Table(name = "utm_session")
public class UtmSessionEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "token", length = 500)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SessionStatus status;

    @Column(name = "connected_at")
    private Date connectedAt;

    @Column(name = "last_heartbeat_at")
    private Date lastHeartbeatAt;

    @Column(name = "expires_at")
    private Date expiresAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;
}
