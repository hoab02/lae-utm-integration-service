package com.viettelpost.fms.utm_integration.airspace.kafka;

import com.viettelpost.fms.utm_integration.airspace.mqtt.dto.NfzMqttMessage;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.config.KafkaTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NfzRawIngressKafkaPublisher {

    private final KafkaMessagePublisher kafkaMessagePublisher;
    private final String topicName;

    public NfzRawIngressKafkaPublisher(KafkaMessagePublisher kafkaMessagePublisher,
                                       KafkaTransportProperties kafkaTransportProperties) {
        this.kafkaMessagePublisher = kafkaMessagePublisher;
        this.topicName = kafkaTransportProperties.getTopics().getNfzUpdates();
        log.info("airspace_nfz_kafka_publisher_init topicName={}", this.topicName);
    }

    public void publish(NfzMqttMessage message) {
        if (topicName == null || topicName.isBlank()) {
            log.error("airspace_nfz_raw_ingress_kafka_publish_skipped reason=missing_topic eventType={}",
                    message.getEventType());
            return;
        }

        log.info("airspace_nfz_raw_ingress_kafka_publish_start topicName={} eventType={} payloadCount={}",
                topicName, message.getEventType(), message.getPayload().size());
        try {
            boolean published = kafkaMessagePublisher.publish(topicName, message);
            log.info("airspace_nfz_raw_ingress_kafka_publish_result topicName={} eventType={} published={}",
                    topicName, message.getEventType(), published);
            if (!published) {
                log.warn("airspace_nfz_raw_ingress_kafka_publish_rejected eventType={} payloadCount={}",
                        message.getEventType(), message.getPayload().size());
            }
        } catch (RuntimeException ex) {
            log.error("airspace_nfz_raw_ingress_kafka_publish_failure eventType={} errorType={}",
                    message.getEventType(), ex.getClass().getSimpleName(), ex);
        }
    }
}
