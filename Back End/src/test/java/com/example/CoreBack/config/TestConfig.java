package com.example.CoreBack.config;

import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestConfig {
    
    @Bean
    @Primary
    public AmqpTemplate amqpTemplate() {
        return Mockito.mock(AmqpTemplate.class);
    }
    
    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
            .build();
    }
}