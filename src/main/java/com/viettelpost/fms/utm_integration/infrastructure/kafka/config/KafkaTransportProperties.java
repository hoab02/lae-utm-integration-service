package com.viettelpost.fms.utm_integration.infrastructure.kafka.config;

import com.viettelpost.fms.utm_integration.infrastructure.kafka.support.KafkaTopicNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.transport.kafka")
public class KafkaTransportProperties {

    private boolean enabled = false;

    private Topics topics = new Topics();

    @Getter
    @Setter
    public static class Topics {

        private String sessionStatusUpdates = KafkaTopicNames.SESSION_STATUS_UPDATES;

        private String pilotRegistrationResultUpdates = KafkaTopicNames.PILOT_REGISTRATION_RESULT_UPDATES;

        private String droneRegistrationResultUpdates = KafkaTopicNames.DRONE_REGISTRATION_RESULT_UPDATES;

        private String flightApprovalResultUpdates = KafkaTopicNames.FLIGHT_APPROVAL_RESULT_UPDATES;

        private String nfzUpdates = KafkaTopicNames.NFZ_UPDATES;

        private String corridorUpdates = KafkaTopicNames.CORRIDOR_UPDATES;
    }
}
