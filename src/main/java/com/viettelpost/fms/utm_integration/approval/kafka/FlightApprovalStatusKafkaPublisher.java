package com.viettelpost.fms.utm_integration.approval.kafka;

import com.viettelpost.fms.common.event.BaseEvent;
import com.viettelpost.fms.utm_integration.approval.dto.kafka.FlightApprovalStatusEvent;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.config.KafkaTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class FlightApprovalStatusKafkaPublisher {

    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final String topicName;

    public FlightApprovalStatusKafkaPublisher(
            KafkaMessagePublisher kafkaMessagePublisher,
            KafkaTransportProperties kafkaTransportProperties
    ) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
        this.topicName = kafkaTransportProperties.getTopics().getFlightApprovalResultUpdates();

        log.info("approval_kafka_publisher_initialized topic={}", topicName);
    }

    public void publish(FlightApprovalStatusEvent event) {
        BaseEvent<FlightApprovalStatusEvent> baseEvent = new BaseEvent<>();
        baseEvent.setEventType("FLIGHT_APPROVAL_STATUS_UPDATED");
        baseEvent.setPayload(event);
        baseEvent.setCreatedBy("fms-utm-integration-service");
        baseEvent.setMetadata(Map.of(
                "topic", topicName,
                "flightTripCode", String.valueOf(event.getFlightTripCode()),
                "status", String.valueOf(event.getStatus())
        ));

        try {
            boolean published = kafkaMessagePublisher.publish(topicName, baseEvent);
            if (!published) {
                log.warn("approval_kafka_publish_failed topic={} planId={} status={} eventId={}",
                        topicName,
                        event.getFlightTripCode(),
                        event.getStatus(),
                        baseEvent.getEventId());
                return;
            }

            log.info("approval_kafka_published topic={} planId={} status={} eventId={}",
                    topicName,
                    event.getFlightTripCode(),
                    event.getStatus(),
                    baseEvent.getEventId());
        } catch (Exception e) {
            log.error("approval_kafka_publish_error topic={} planId={} status={} eventId={}",
                    topicName,
                    event.getFlightTripCode(),
                    event.getStatus(),
                    baseEvent.getEventId(),
                    e);
        }
    }
}