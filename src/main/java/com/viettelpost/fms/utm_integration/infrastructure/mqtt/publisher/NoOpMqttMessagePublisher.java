package com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class NoOpMqttMessagePublisher implements MqttMessagePublisher {

    public static final NoOpMqttMessagePublisher INSTANCE = new NoOpMqttMessagePublisher();

    private NoOpMqttMessagePublisher() {
    }

    @Override
    public boolean publish(String topicName, Object payload) {
        log.debug("mqtt_publish_skipped topicName={} payloadType={} reason=no_op_transport",
                topicName, payload != null ? payload.getClass().getSimpleName() : null);
        return false;
    }

    @Override
    public boolean publish(String topicName, Object payload, String username, String password) {
        log.debug("mqtt_publish_skipped topicName={} payloadType={} username={} reason=no_op_transport",
                topicName,
                payload != null ? payload.getClass().getSimpleName() : null,
                username);
        return false;
    }
}