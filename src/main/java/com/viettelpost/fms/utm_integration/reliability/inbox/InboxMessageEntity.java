package com.viettelpost.fms.utm_integration.reliability.inbox;

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
@Table(name = "inbox_message", uniqueConstraints = {
        @UniqueConstraint(name = "uk_inbox_channel_message_key", columnNames = {"channel", "message_key"})
})
public class InboxMessageEntity extends Auditor {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "channel", nullable = false, length = 100)
    private String channel;

    @Column(name = "message_key", nullable = false, length = 200)
    private String messageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private InboxMessageStatus status;

    @Column(name = "received_at", nullable = false)
    private Date receivedAt;

    @Column(name = "processed_at")
    private Date processedAt;
}