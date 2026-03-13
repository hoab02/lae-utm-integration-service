package com.viettelpost.fms.utm_integration.command.domain;

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
@Table(name = "utm_command")
public class UtmCommandEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "command_id", nullable = false, updatable = false, length = 100, unique = true)
    private String commandId;

    @Column(name = "mission_id", length = 100)
    private String missionId;

    @Column(name = "command_type", nullable = false, length = 100)
    private String commandType;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "payload", length = 4000)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CommandStatus status;

    @Column(name = "received_at", nullable = false)
    private Date receivedAt;

    @Column(name = "ack_at")
    private Date ackAt;

    @Column(name = "executed_at")
    private Date executedAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;
}
