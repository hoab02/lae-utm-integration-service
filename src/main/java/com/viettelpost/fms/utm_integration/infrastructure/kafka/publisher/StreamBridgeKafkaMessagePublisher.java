package com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;

@RequiredArgsConstructor
@Slf4j
public class StreamBridgeKafkaMessagePublisher implements KafkaMessagePublisher {

    private final StreamBridge streamBridge;

    @Override
    public boolean publish(String topicName, Object payload) {
        log.info("kafka_publish_start topicName={} payloadType={}",
                topicName, payload != null ? payload.getClass().getSimpleName() : null);

        boolean published = streamBridge.send(topicName, payload);

        log.info("kafka_publish_result topicName={} published={}",
                topicName, published);

        return published;
    }
}
