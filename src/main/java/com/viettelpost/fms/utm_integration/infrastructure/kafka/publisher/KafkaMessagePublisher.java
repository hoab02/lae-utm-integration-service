package com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher;

public interface KafkaMessagePublisher {

    boolean publish(String topicName, Object payload);
}
