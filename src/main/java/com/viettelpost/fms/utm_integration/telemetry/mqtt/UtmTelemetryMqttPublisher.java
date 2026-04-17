package com.viettelpost.fms.utm_integration.telemetry.mqtt;

import com.viettelpost.fms.utm_integration.infrastructure.mqtt.config.MqttTransportProperties;
import com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher.MqttMessagePublisher;
import com.viettelpost.fms.utm_integration.session.domain.SessionStatus;
import com.viettelpost.fms.utm_integration.session.dto.internal.UtmSessionContextDto;
import com.viettelpost.fms.utm_integration.session.service.UtmSessionService;
import com.viettelpost.fms.utm_integration.telemetry.client.UtmTelemetryPublisher;
import com.viettelpost.fms.utm_integration.telemetry.dto.TelemetryMessage;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UtmTelemetryMqttPublisher implements UtmTelemetryPublisher {

    private final UtmSessionService utmSessionService;
    private final MqttMessagePublisher mqttMessagePublisher;
    private final MqttTransportProperties mqttTransportProperties;
    private final MeterRegistry meterRegistry;

    @Override
    public void publish(TelemetryMessage message) {
        String accessToken = getRequiredValidAccessToken();
        String topic = mqttTransportProperties.getTopics().getTelemetryUplink();
        String username = mqttTransportProperties.getUsername();

        boolean published = mqttMessagePublisher.publish(topic, message, username, accessToken);

        if (!published) {
            meterRegistry.counter("utm.telemetry.publish.failed").increment();

            log.error("telemetry_mqtt_publish_failed topic={} droneId={} tenantId={}",
                    topic,
                    message.getDroneId(),
                    message.getTenantId());

            throw new IllegalStateException("Failed to publish telemetry to MQTT topic: " + topic);
        }

        meterRegistry.counter("utm.telemetry.publish.success").increment();

        log.info("telemetry_mqtt_publish_success topic={} droneId={} tenantId={}",
                topic,
                message.getDroneId(),
                message.getTenantId());
    }

    private String getRequiredValidAccessToken() {
        utmSessionService.refreshIfNeeded();

        UtmSessionContextDto sessionContext = utmSessionService.getCurrentSessionContext();
        if (sessionContext == null) {
            throw new IllegalStateException("UTM session context is null");
        }

        if (!SessionStatus.CONNECTED.equals(sessionContext.status())) {
            throw new IllegalStateException("UTM session is not connected. Current status: " + sessionContext.status());
        }

        if (sessionContext.accessToken() == null || sessionContext.accessToken().isBlank()) {
            throw new IllegalStateException("UTM access token is missing");
        }

        return sessionContext.accessToken();
    }
}