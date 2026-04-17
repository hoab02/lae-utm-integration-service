package com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.infrastructure.mqtt.config.MqttTransportProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class PahoMqttMessagePublisher implements MqttMessagePublisher {

    private final ObjectMapper objectMapper;
    private final MqttTransportProperties properties;

    @Override
    public boolean publish(String topicName, Object payload) {
        return publish(topicName, payload, properties.getUsername(), properties.getPassword());
    }

    @Override
    public boolean publish(String topicName, Object payload, String username, String password) {
        log.info("mqtt_publish_start topicName={} payloadType={} username={}",
                topicName,
                payload != null ? payload.getClass().getSimpleName() : null,
                username);

        String payloadJson = toJson(payload);
        String clientId = buildOutboundClientId();

        try {
            MqttClient client = new MqttClient(properties.getBrokerUrl(), clientId);
            MqttConnectOptions options = buildConnectOptions(username, password);

            client.connect(options);

            MqttMessage mqttMessage = new MqttMessage(payloadJson.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(properties.getQos());

            client.publish(topicName, mqttMessage);
            client.disconnect();
            client.close();

            log.info("mqtt_publish_success topicName={} clientId={}", topicName, clientId);
            return true;
        } catch (MqttException ex) {
            log.error("mqtt_publish_failure topicName={} clientId={} errorType={}",
                    topicName, clientId, ex.getClass().getSimpleName(), ex);
            return false;
        }
    }

    private MqttConnectOptions buildConnectOptions(String username, String password) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getBrokerUrl()});
        options.setCleanSession(true);
        options.setAutomaticReconnect(false);
        options.setConnectionTimeout(5);
        options.setKeepAliveInterval(30);

        if (username != null && !username.isBlank()) {
            options.setUserName(username);
        }

        if (password != null && !password.isBlank()) {
            options.setPassword(password.toCharArray());
        }

        return options;
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to serialize MQTT payload", ex);
        }
    }

    private String buildOutboundClientId() {
        return properties.getClientId() + "-out-" + UUID.randomUUID().toString().substring(0, 8);
    }
}