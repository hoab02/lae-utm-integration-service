package com.viettelpost.fms.utm_integration.infrastructure.mqtt.config;

import com.viettelpost.fms.utm_integration.infrastructure.mqtt.support.MqttTopicNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.transport.mqtt")
public class MqttTransportProperties {

    private boolean enabled = false;

    private String brokerUrl;

    private String username;

    private String password;

    private String clientId = "fms-utm-integration-service";

    private int qos = 1;

    private Topics topics = new Topics();

    private Subscribers subscribers = new Subscribers();

    @Getter
    @Setter
    public static class Topics {

        private String telemetryUplink = MqttTopicNames.TELEMETRY_UPLINK;

        private String commandDownlink = MqttTopicNames.COMMAND_DOWNLINK;

        private String commandAckUplink = MqttTopicNames.COMMAND_ACK_UPLINK;

        private String airspaceNfzUpdates = MqttTopicNames.AIRSPACE_NFZ_UPDATES;

        private String airspaceCorridorUpdates = MqttTopicNames.AIRSPACE_CORRIDOR_UPDATES;

        private String flightApprovalStatusUpdates = MqttTopicNames.FLIGHT_APPROVAL_STATUS_UPDATES;
    }

    @Getter
    @Setter
    public static class Subscribers {

        private boolean airspaceEnabled = false;

        private boolean approvalEnabled = false;
    }
}
