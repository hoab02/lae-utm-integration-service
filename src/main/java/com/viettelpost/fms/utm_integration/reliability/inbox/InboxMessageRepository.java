package com.viettelpost.fms.utm_integration.reliability.inbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InboxMessageRepository extends JpaRepository<InboxMessageEntity, String> {

    Optional<InboxMessageEntity> findByChannelAndMessageKey(String channel, String messageKey);
}