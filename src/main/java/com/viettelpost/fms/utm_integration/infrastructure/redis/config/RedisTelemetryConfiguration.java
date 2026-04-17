package com.viettelpost.fms.utm_integration.infrastructure.redis.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisTelemetryProperties.class)
public class RedisTelemetryConfiguration {
}
