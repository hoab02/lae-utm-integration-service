package com.viettelpost.fms.utm_integration.infrastructure.redis.config;

import com.viettelpost.fms.utm_integration.infrastructure.redis.support.RedisChannelNames;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.transport.redis")
public class RedisTelemetryProperties {

    private boolean enabled = false;

    private Channels channels = new Channels();

    @Getter
    @Setter
    public static class Channels {

        private String telemetryInbound = RedisChannelNames.TELEMETRY_INBOUND;
    }
}
