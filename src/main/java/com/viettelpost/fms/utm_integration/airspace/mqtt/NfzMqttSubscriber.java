package com.viettelpost.fms.utm_integration.airspace.mqtt;

import com.viettelpost.fms.utm_integration.airspace.kafka.NfzRawIngressKafkaPublisher;
import com.viettelpost.fms.utm_integration.airspace.mqtt.dto.NfzMqttMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NfzMqttSubscriber {

    private final NfzRawIngressKafkaPublisher nfzRawIngressKafkaPublisher;

    public void receive(NfzMqttMessage message) {
        if (message == null) {
            log.warn("airspace_nfz_receive_skipped reason=null_message");
            return;
        }
        if (isBlank(message.getEventType())) {
            log.warn("airspace_nfz_receive_skipped reason=blank_event_type");
            return;
        }
        if (message.getPayload() == null || message.getPayload().isEmpty()) {
            log.warn("airspace_nfz_receive_skipped eventType={} reason=empty_payload",
                    message.getEventType());
            return;
        }
        if (message.getPayload().stream().anyMatch(this::isInvalidNfzItem)) {
            log.warn("airspace_nfz_receive_skipped eventType={} reason=invalid_payload_items payloadCount={}",
                    message.getEventType(), message.getPayload().size());
            return;
        }

        log.info("airspace_nfz_receive_start eventType={} payloadCount={}",
                message.getEventType(), message.getPayload().size());
        nfzRawIngressKafkaPublisher.publish(message);
        log.info("airspace_nfz_receive_finish eventType={} payloadCount={}",
                message.getEventType(), message.getPayload().size());
    }

    private boolean isInvalidNfzItem(NfzMqttMessage.NfzItem item) {
        return item == null
                || isBlank(item.getGeoId())
                || isBlank(item.getType())
                || item.getGeometry() == null
                || item.getGeometry().isNull();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
