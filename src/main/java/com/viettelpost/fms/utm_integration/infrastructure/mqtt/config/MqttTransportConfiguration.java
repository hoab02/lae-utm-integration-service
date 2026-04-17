package com.viettelpost.fms.utm_integration.infrastructure.mqtt.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.utm_integration.airspace.mqtt.CorridorMqttSubscriber;
import com.viettelpost.fms.utm_integration.airspace.mqtt.NfzMqttSubscriber;
import com.viettelpost.fms.utm_integration.airspace.mqtt.dto.CorridorMqttMessage;
import com.viettelpost.fms.utm_integration.airspace.mqtt.dto.NfzMqttMessage;
import com.viettelpost.fms.utm_integration.approval.mqtt.FlightApprovalStatusMqttSubscriber;
import com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher.MqttMessagePublisher;
import com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher.NoOpMqttMessagePublisher;
import com.viettelpost.fms.utm_integration.infrastructure.mqtt.publisher.PahoMqttMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;

@Configuration
@EnableConfigurationProperties(MqttTransportProperties.class)
@Slf4j
public class MqttTransportConfiguration {

    public static final String AIRSPACE_CORRIDOR_INPUT_CHANNEL = "airspaceCorridorMqttInputChannel";
    public static final String AIRSPACE_NFZ_INPUT_CHANNEL = "airspaceNfzMqttInputChannel";
    public static final String FLIGHT_APPROVAL_STATUS_INPUT_CHANNEL = "flightApprovalStatusMqttInputChannel";

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    MqttMessagePublisher mqttMessagePublisher(ObjectMapper objectMapper,
                                              MqttTransportProperties properties) {
        Assert.hasText(properties.getBrokerUrl(),
                "app.transport.mqtt.broker-url must not be blank when MQTT is enabled");

        return new PahoMqttMessagePublisher(objectMapper, properties);
    }

    @Bean
    @ConditionalOnMissingBean(MqttMessagePublisher.class)
    MqttMessagePublisher noOpMqttMessagePublisher() {
        return NoOpMqttMessagePublisher.INSTANCE;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    MqttPahoClientFactory mqttPahoClientFactory(MqttTransportProperties properties) {
        Assert.hasText(properties.getBrokerUrl(),
                "app.transport.mqtt.broker-url must not be blank when MQTT is enabled");

        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getBrokerUrl()});

        if (properties.getUsername() != null && !properties.getUsername().isBlank()) {
            options.setUserName(properties.getUsername());
        }

        if (properties.getPassword() != null && !properties.getPassword().isBlank()) {
            options.setPassword(properties.getPassword().toCharArray());
        }

        factory.setConnectionOptions(options);

        log.info("mqtt_client_factory_init brokerUrl={} clientId={} usernameConfigured={}",
                properties.getBrokerUrl(),
                properties.getClientId(),
                properties.getUsername() != null && !properties.getUsername().isBlank());

        return factory;
    }

    @Bean(name = AIRSPACE_CORRIDOR_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageChannel airspaceCorridorMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean(name = AIRSPACE_NFZ_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageChannel airspaceNfzMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean(name = FLIGHT_APPROVAL_STATUS_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "approval-enabled", havingValue = "true")
    MessageChannel flightApprovalStatusMqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageProducer airspaceCorridorInboundAdapter(MqttPahoClientFactory mqttPahoClientFactory,
                                                   MqttTransportProperties properties) {
        String topic = properties.getTopics().getAirspaceCorridorUpdates();
        Assert.hasText(topic, "app.transport.mqtt.topics.airspace-corridor-updates must not be blank when airspace MQTT is enabled");
        String clientId = inboundClientId(properties, "airspace-corridor");

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory, topic);
        adapter.setCompletionTimeout(5_000);
        adapter.setQos(properties.getQos());
        adapter.setConverter(stringPayloadConverter());
        adapter.setOutputChannelName(AIRSPACE_CORRIDOR_INPUT_CHANNEL);
        log.info("mqtt_airspace_corridor_inbound_adapter_init topic={} qos={}", topic, properties.getQos());
        return adapter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageProducer airspaceNfzInboundAdapter(MqttPahoClientFactory mqttPahoClientFactory,
                                              MqttTransportProperties properties) {
        String topic = properties.getTopics().getAirspaceNfzUpdates();
        Assert.hasText(topic, "app.transport.mqtt.topics.airspace-nfz-updates must not be blank when airspace MQTT is enabled");

        String clientId = inboundClientId(properties, "airspace-nfz");
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory, topic);
        adapter.setCompletionTimeout(5_000);
        adapter.setQos(properties.getQos());
        adapter.setConverter(stringPayloadConverter());
        adapter.setOutputChannelName(AIRSPACE_NFZ_INPUT_CHANNEL);
        log.info("mqtt_airspace_nfz_inbound_adapter_init topic={} qos={}", topic, properties.getQos());
        return adapter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "approval-enabled", havingValue = "true")
    MessageProducer flightApprovalStatusInboundAdapter(MqttPahoClientFactory mqttPahoClientFactory,
                                                       MqttTransportProperties properties) {
        String topic = properties.getTopics().getFlightApprovalStatusUpdates();
        Assert.hasText(topic,
                "app.transport.mqtt.topics.flight-approval-status-updates must not be blank when approval MQTT is enabled");

        String clientId = inboundClientId(properties, "flight-approval-status");
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory, topic);
        adapter.setCompletionTimeout(5_000);
        adapter.setQos(properties.getQos());
        adapter.setConverter(stringPayloadConverter());
        adapter.setOutputChannelName(FLIGHT_APPROVAL_STATUS_INPUT_CHANNEL);
        log.info("mqtt_flight_approval_status_inbound_adapter_init topic={} qos={}", topic, properties.getQos());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = AIRSPACE_CORRIDOR_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageHandler airspaceCorridorInboundHandler(ObjectMapper objectMapper,
                                                  CorridorMqttSubscriber corridorMqttSubscriber) {
        return message -> handleAirspaceMessage(message, objectMapper, CorridorMqttMessage.class,
                "airspace_corridor", corridorMqttSubscriber::receive);
    }

    @Bean
    @ServiceActivator(inputChannel = AIRSPACE_NFZ_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "airspace-enabled", havingValue = "true")
    MessageHandler airspaceNfzInboundHandler(ObjectMapper objectMapper,
                                             NfzMqttSubscriber nfzMqttSubscriber) {
        return message -> handleAirspaceMessage(message, objectMapper, NfzMqttMessage.class,
                "airspace_nfz", nfzMqttSubscriber::receive);
    }

    @Bean
    @ServiceActivator(inputChannel = FLIGHT_APPROVAL_STATUS_INPUT_CHANNEL)
    @ConditionalOnProperty(prefix = "app.transport.mqtt", name = "enabled", havingValue = "true")
    @ConditionalOnProperty(prefix = "app.transport.mqtt.subscribers", name = "approval-enabled", havingValue = "true")
    MessageHandler flightApprovalStatusInboundHandler(FlightApprovalStatusMqttSubscriber flightApprovalStatusMqttSubscriber) {
        return message -> {
            String payload = toPayloadString(message.getPayload());
            if (payload == null || payload.isBlank()) {
                log.warn("approval_mqtt_inbound_skipped reason=empty_payload");
                return;
            }

            log.info("approval_mqtt_inbound_receive_start payloadLength={}", payload.length());
            try {
                flightApprovalStatusMqttSubscriber.receive(payload);
                log.info("approval_mqtt_inbound_receive_finish payloadLength={}", payload.length());
            } catch (RuntimeException ex) {
                log.error("approval_mqtt_inbound_handle_failure errorType={} payload={}",
                        ex.getClass().getSimpleName(), payload, ex);
            }
        };
    }

    private DefaultPahoMessageConverter stringPayloadConverter() {
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(false);
        return converter;
    }

    private String inboundClientId(MqttTransportProperties properties, String suffix) {
        return properties.getClientId() + "-" + suffix;
    }

    private <T> void handleAirspaceMessage(Message<?> message,
                                           ObjectMapper objectMapper,
                                           Class<T> targetType,
                                           String logPrefix,
                                           java.util.function.Consumer<T> consumer) {
        String payload = toPayloadString(message.getPayload());
        if (payload == null || payload.isBlank()) {
            log.warn("{}_mqtt_inbound_skipped reason=empty_payload", logPrefix);
            return;
        }

        log.info("{}_mqtt_inbound_receive_start payloadLength={}", logPrefix, payload.length());
        try {
            T dto = objectMapper.readValue(payload, targetType);
            consumer.accept(dto);
            log.info("{}_mqtt_inbound_receive_finish payloadLength={}", logPrefix, payload.length());
        } catch (JsonProcessingException ex) {
            log.error("{}_mqtt_inbound_deserialize_failure errorType={} payload={}",
                    logPrefix, ex.getClass().getSimpleName(), payload, ex);
        } catch (RuntimeException ex) {
            log.error("{}_mqtt_inbound_handle_failure errorType={} payload={}",
                    logPrefix, ex.getClass().getSimpleName(), payload, ex);
        }
    }

    private String toPayloadString(Object payload) {
        if (payload instanceof byte[] bytes) {
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        }
        return payload != null ? payload.toString() : null;
    }
}