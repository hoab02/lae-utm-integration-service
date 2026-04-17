package com.viettelpost.fms.utm_integration.infrastructure.redis.config;

import com.viettelpost.fms.utm_integration.telemetry.redis.TelemetryRedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.transport.redis", name = "enabled", havingValue = "true")
public class RedisPubSubConfiguration {

    private final RedisTelemetryProperties redisTelemetryProperties;

    @Bean
    MessageListenerAdapter telemetryRedisListenerAdapter(TelemetryRedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "receive");
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter telemetryRedisListenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
                telemetryRedisListenerAdapter,
                new ChannelTopic(redisTelemetryProperties.getChannels().getTelemetryInbound())
        );
        return container;
    }
}