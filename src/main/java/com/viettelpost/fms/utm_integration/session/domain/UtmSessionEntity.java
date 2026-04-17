package com.viettelpost.fms.utm_integration.session.domain;

import com.viettelpost.fms.utm_integration.domain.Auditor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "dcs_id", length = 100)
    private String dcsId;

    @Column(name = "access_token", length = 4000)
    private String accessToken;

    @Column(name = "refresh_token", length = 4000)
    private String refreshToken;

    @Column(name = "token_type", length = 100)
    private String tokenType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "access_token_expires_at")
    private Date accessTokenExpiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "refresh_token_expires_at")
    private Date refreshTokenExpiresAt;

    @Column(name = "session_state", length = 255)
    private String sessionState;

    @Column(name = "scope", length = 1000)
    private String scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private SessionStatus status;

    @Column(name = "active")
    private Boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "connected_at")
    private Date connectedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_refresh_at")
    private Date lastRefreshAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "disconnected_at")
    private Date disconnectedAt;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;
}