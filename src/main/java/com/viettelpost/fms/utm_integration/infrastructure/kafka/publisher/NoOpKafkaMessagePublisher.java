package com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class NoOpKafkaMessagePublisher implements KafkaMessagePublisher {

    public static final NoOpKafkaMessagePublisher INSTANCE = new NoOpKafkaMessagePublisher();

    private NoOpKafkaMessagePublisher() {
    }

    @Override
    public boolean publish(String topicName, Object payload) {
        log.debug("kafka_publish_skipped topicName={} payloadType={} reason=no_op_transport",
                topicName, payload != null ? payload.getClass().getSimpleName() : null);
        return false;
    }
}
