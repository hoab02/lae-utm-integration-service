package com.viettelpost.fms.utm_integration.command.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.command.dto.UtmCommandStatusDto;
import com.viettelpost.fms.utm_integration.reliability.outbox.OutboxService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoOpUtmCommandAckService implements UtmCommandAckService {

    private static final String COMMAND_ACK_REQUESTED = "COMMAND_ACK_REQUESTED";
    private static final String UTM_COMMAND_AGGREGATE = "UTM_COMMAND";

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Override
    public void prepareAck(UtmCommandStatusDto command) {
        log.info("command_ack_enqueue_start commandId={} missionId={} status={}",
                command.getCommandId(), command.getMissionId(), command.getStatus());
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            outboxService.enqueue(COMMAND_ACK_REQUESTED, UTM_COMMAND_AGGREGATE, command.getCommandId(), toPayload(command));
            log.info("command_ack_enqueue_success commandId={} missionId={}", command.getCommandId(), command.getMissionId());
        } catch (RuntimeException ex) {
            log.error("command_ack_enqueue_failure commandId={} missionId={} errorType={}",
                    command.getCommandId(), command.getMissionId(), ex.getClass().getSimpleName(), ex);
            throw ex;
        } finally {
            sample.stop(meterRegistry.timer("utm.command.ack.enqueue.latency"));
        }
    }

    private String toPayload(UtmCommandStatusDto command) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("command_id", command.getCommandId());
        payload.put("mission_id", command.getMissionId());
        payload.put("status", command.getStatus());
        payload.put("received_at", command.getReceivedAt());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize command acknowledgement payload", ex);
        }
    }
}