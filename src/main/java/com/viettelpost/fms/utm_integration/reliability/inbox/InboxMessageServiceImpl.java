package com.viettelpost.fms.utm_integration.reliability.inbox;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InboxMessageServiceImpl implements InboxMessageService {

    private final InboxMessageRepository inboxMessageRepository;

    @Override
    @Transactional
    public boolean firstSeen(String channel, String messageKey) {
        if (inboxMessageRepository.findByChannelAndMessageKey(channel, messageKey).isPresent()) {
            return false;
        }

        try {
            inboxMessageRepository.save(InboxMessageEntity.builder()
                    .channel(channel)
                    .messageKey(messageKey)
                    .status(InboxMessageStatus.RECEIVED)
                    .receivedAt(new Date())
                    .build());
            return true;
        } catch (DataIntegrityViolationException ex) {
            return false;
        }
    }

    @Override
    @Transactional
    public void markProcessed(String channel, String messageKey) {
        inboxMessageRepository.findByChannelAndMessageKey(channel, messageKey)
                .ifPresent(message -> {
                    message.setStatus(InboxMessageStatus.PROCESSED);
                    message.setProcessedAt(new Date());
                    inboxMessageRepository.save(message);
                });
    }
}