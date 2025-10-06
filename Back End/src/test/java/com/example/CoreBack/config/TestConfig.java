package com.example.CoreBack.config;

import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public AmqpTemplate amqpTemplate() {
        return Mockito.mock(AmqpTemplate.class);
    }
    
    @Bean
    @Primary
    public HttpSecurity httpSecurity() {
        return Mockito.mock(HttpSecurity.class);
    }
}