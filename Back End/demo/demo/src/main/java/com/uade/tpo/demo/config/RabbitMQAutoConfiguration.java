package com.uade.tpo.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMQAutoConfiguration {
    // This configuration will only be active when RabbitMQ is enabled
    // The RabbitMQConfig and EventConfig will be automatically included when this condition is met
}
