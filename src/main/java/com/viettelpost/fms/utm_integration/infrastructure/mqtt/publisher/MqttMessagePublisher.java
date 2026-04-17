package com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher;

public interface MqttMessagePublisher {

    boolean publish(String topicName, Object payload);

    boolean publish(String topicName, Object payload, String username, String password);
}