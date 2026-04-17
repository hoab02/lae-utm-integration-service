package com.viettelpost.fms.utm_integration.airspace.mqtt;

import com.viettelpost.fms.utm_integration.airspace.kafka.CorridorRawIngressKafkaPublisher;
import com.viettelpost.fms.utm_integration.airspace.mqtt.dto.CorridorMqttMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CorridorMqttSubscriber {

    private final CorridorRawIngressKafkaPublisher corridorRawIngressKafkaPublisher;

    public void receive(CorridorMqttMessage message) {
        if (message == null) {
            log.warn("airspace_corridor_receive_skipped reason=null_message");
            return;
        }
        if (isBlank(message.getEventType())) {
            log.warn("airspace_corridor_receive_skipped reason=blank_event_type");
            return;
        }
        if (message.getPayload() == null || message.getPayload().isEmpty()) {
            log.warn("airspace_corridor_receive_skipped eventType={} reason=empty_payload",
                    message.getEventType());
            return;
        }
        if (message.getPayload().stream().anyMatch(this::isInvalidCorridorItem)) {
            log.warn("airspace_corridor_receive_skipped eventType={} reason=invalid_payload_items payloadCount={}",
                    message.getEventType(), message.getPayload().size());
            return;
        }

        log.info("airspace_corridor_receive_start eventType={} payloadCount={}",
                message.getEventType(), message.getPayload().size());
        corridorRawIngressKafkaPublisher.publish(message);
        log.info("airspace_corridor_receive_finish eventType={} payloadCount={}",
                message.getEventType(), message.getPayload().size());
    }

    private boolean isInvalidCorridorItem(CorridorMqttMessage.CorridorItem item) {
        return item == null
                || isBlank(item.getCorridorId())
                || item.getWaypoint() == null
                || item.getWaypoint().isEmpty();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
