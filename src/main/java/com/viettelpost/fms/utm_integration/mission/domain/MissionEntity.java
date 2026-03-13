package com.viettelpost.fms.utm_integration.mission.domain;

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
@Table(name = "mission_runtime")
public class MissionEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "mission_id", nullable = false, length = 100, unique = true)
    private String missionId;

    @Column(name = "plan_id", nullable = false, length = 100)
    private String planId;

    @Column(name = "drone_id", length = 100)
    private String droneId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private MissionState state;

    @Column(name = "airborne_at")
    private Date airborneAt;

    @Column(name = "landing_at")
    private Date landingAt;

    @Column(name = "completed_at")
    private Date completedAt;

    @Column(name = "emergency_flag", nullable = false)
    private boolean emergencyFlag;

    @Column(name = "emergency_reason", length = 500)
    private String emergencyReason;
}