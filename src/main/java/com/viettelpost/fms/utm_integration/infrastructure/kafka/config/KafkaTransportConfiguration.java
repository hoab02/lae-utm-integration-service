package com.viettelpost.fms.utm_integration.infrastructure.kafka.config;

import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.KafkaMessagePublisher;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.NoOpKafkaMessagePublisher;
import com.viettelpost.fms.utm_integration.infrastructure.kafka.publisher.StreamBridgeKafkaMessagePublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaTransportProperties.class)
public class KafkaTransportConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.kafka", name = "enabled", havingValue = "true")
    KafkaMessagePublisher kafkaMessagePublisher(StreamBridge streamBridge) {
        return new StreamBridgeKafkaMessagePublisher(streamBridge);
    }

    @Bean
    @ConditionalOnMissingBean(KafkaMessagePublisher.class)
    KafkaMessagePublisher noOpKafkaMessagePublisher() {
        return NoOpKafkaMessagePublisher.INSTANCE;
    }
}
