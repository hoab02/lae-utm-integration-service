package com.viettelpost.fms.utm_integration.registry.kafka;

import com.viettelpost.fms.utm_integration.infrastructure.kafka.config.KafkaTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import com.viettelpost.fms.utm_integration.registry.dto.DroneRegistrationStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DroneRegistrationResultKafkaPublisher {

    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final String topicName;

    public DroneRegistrationResultKafkaPublisher(KafkaMessagePublisher kafkaMessagePublisher,
                                                 KafkaTransportProperties kafkaTransportProperties) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
        this.topicName = kafkaTransportProperties.getTopics().getDroneRegistrationResultUpdates();
        log.info("drone_registration_kafka_publisher_init topicName={}", this.topicName);
    }

    public void publish(DroneRegistrationStatusDto status) {
        if (topicName == null || topicName.isBlank()) {
            log.error("drone_registration_kafka_publish_skipped_missing_topic syncStatus={}",
                    status.getSyncStatus());
            return;
        }
        try {
            log.info("drone_registration_kafka_publish_start utmDroneId={} syncStatus={} status={} topic={}",
                    status.getUtmDroneId(), status.getSyncStatus(), status.getStatus(), topicName);
            boolean published = kafkaMessagePublisher.publish(topicName, status);
            log.info("drone_registration_kafka_publish_result syncStatus={} topic={} published={}",
                    status.getSyncStatus(), topicName, published);
            if (!published) {
                log.warn("drone_registration_kafka_publish_rejected syncStatus={} status={}",
                        status.getSyncStatus(), status.getStatus());
            }
        } catch (RuntimeException ex) {
            log.error("drone_registration_kafka_publish_failure syncStatus={} status={} errorType={}",
                    status.getSyncStatus(), status.getStatus(), ex.getClass().getSimpleName(), ex);
        }
    }
}