package com.viettelpost.fms.utm_integration.approval.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.approval.dto.utm.inbound.UtmFlightApprovalStatusMessage;
import com.viettelpost.fms.utm_integration.approval.service.FlightApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "app.transport.mqtt.subscribers",
        name = "approval-enabled",
        havingValue = "true"
)
public class FlightApprovalStatusMqttSubscriber {

    private final ObjectMapper objectMapper;
    private final FlightApprovalService flightApprovalService;

    public void receive(String payload) {
        try {
            UtmFlightApprovalStatusMessage message =
                    objectMapper.readValue(payload, UtmFlightApprovalStatusMessage.class);

            log.info("approval_mqtt_status_received eventType={} objectId={} objectName={} eventStatus={}",
                    message.getEventType(),
                    message.getObjectId(),
                    message.getObjectName(),
                    message.getEventStatus());

            flightApprovalService.handleUtmStatus(message);
        } catch (Exception e) {
            log.error("approval_mqtt_status_process_failed payload={}", payload, e);
        }
    }
}