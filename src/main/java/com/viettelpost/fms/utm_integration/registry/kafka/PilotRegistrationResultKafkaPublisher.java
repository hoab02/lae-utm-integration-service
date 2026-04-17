package com.viettelpost.fms.utm_integration.registry.kafka;

import com.viettelpost.fms.utm_integration.infrastructure.kafka.config.KafkaTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import com.viettelpost.fms.utm_integration.registry.dto.PilotRegistrationStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PilotRegistrationResultKafkaPublisher {

    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final String topicName;

    public PilotRegistrationResultKafkaPublisher(KafkaMessagePublisher kafkaMessagePublisher,
                                                 KafkaTransportProperties kafkaTransportProperties) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
        this.topicName = kafkaTransportProperties.getTopics().getPilotRegistrationResultUpdates();
        log.info("pilot_registration_kafka_publisher_init topicName={}", this.topicName);
    }

    public void publish(PilotRegistrationStatusDto status) {
        if (topicName == null || topicName.isBlank()) {
            log.error("pilot_registration_kafka_publish_skipped_missing_topic syncStatus={}",
                    status.getSyncStatus());
            return;
        }
        try {
            log.info("pilot_registration_kafka_publish_start utmPilotId={} syncStatus={} status={} topic={}",
                    status.getUtmPilotId(), status.getSyncStatus(), status.getStatus(), topicName);
            boolean published = kafkaMessagePublisher.publish(topicName, status);
            log.info("pilot_registration_kafka_publish_result syncStatus={} topic={} published={}",
                    status.getSyncStatus(), topicName, published);
            if (!published) {
                log.warn("pilot_registration_kafka_publish_rejected syncStatus={} status={}",
                        status.getSyncStatus(), status.getStatus());
            }
        } catch (RuntimeException ex) {
            log.error("pilot_registration_kafka_publish_failure pilotId={} syncStatus={} status={} errorType={}",
                    status.getSyncStatus(), status.getStatus(), ex.getClass().getSimpleName(), ex);
        }
    }
}