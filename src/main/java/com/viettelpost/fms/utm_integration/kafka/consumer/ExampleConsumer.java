package com.viettelpost.fms.utm_integration.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettelpost.fms.common.event.BaseEvent;
import com.viettelpost.fms.utm_integration.kafka.event.ExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class ExampleConsumer {

    @Bean
    public Consumer<BaseEvent<ExampleEvent>> exampleCreated() {
        return event -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                log.info("Event process : {}", mapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        };
    }
}
