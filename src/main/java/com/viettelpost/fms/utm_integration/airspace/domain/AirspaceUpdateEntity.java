package com.viettelpost.fms.utm_integration.airspace.domain;

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
@Table(name = "airspace_update")
public class AirspaceUpdateEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "update_id", nullable = false, updatable = false, length = 100, unique = true)
    private String updateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private AirspaceUpdateType type;

    @Column(name = "version", length = 100)
    private String version;

    @Column(name = "payload", nullable = false, length = 4000)
    private String payload;

    @Column(name = "effective_from")
    private Date effectiveFrom;

    @Column(name = "received_at", nullable = false)
    private Date receivedAt;

    @Column(name = "source", length = 100)
    private String source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private AirspaceUpdateStatus status;
}
