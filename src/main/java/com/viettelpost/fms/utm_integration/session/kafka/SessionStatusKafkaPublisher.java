package com.viettelpost.fms.utm_integration.session.kafka;

import com.viettelpost.fms.utm_integration.infrastructure.kafka.config.KafkaTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionStatusKafkaPublisher {

    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final String topicName;

    public SessionStatusKafkaPublisher(KafkaMessagePublisher kafkaMessagePublisher,
                                       KafkaTransportProperties kafkaTransportProperties) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
        this.topicName = kafkaTransportProperties.getTopics().getSessionStatusUpdates();
        log.info("session_status_kafka_publisher_init topicName={}", this.topicName);
    }

    public void publish(UtmSessionStatusDto status) {
        if (topicName == null || topicName.isBlank()) {
            log.error("session_status_kafka_publish_skipped_missing_topic dcsId={} status={}",
                    status.dcsId(), status.status());
            return;
        }
        try {
            log.info("session_status_kafka_publish_start dcsId={} status={} topic={}",
                    status.dcsId(), status.status(), topicName);

            boolean published = kafkaMessagePublisher.publish(topicName, status);

            log.info("session_status_kafka_publish_result dcsId={} status={} topic={} published={}",
                    status.dcsId(), status.status(), topicName, published);

            if (!published) {
                log.warn("session_status_kafka_publish_rejected dcsId={} status={}",
                        status.dcsId(), status.status());
            }
        } catch (RuntimeException ex) {
            log.error("session_status_kafka_publish_failure dcsId={} status={} errorType={}",
                    status.dcsId(), status.status(), ex.getClass().getSimpleName(), ex);
        }
    }
}
