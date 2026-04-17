package com.viettelpost.fms.utm_integration.reliability.inbox;

public interface InboxMessageService {

    boolean firstSeen(String channel, String messageKey);

    void markProcessed(String channel, String messageKey);
}