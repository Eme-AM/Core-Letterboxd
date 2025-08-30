package com.example.CoreBack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()  // desactiva CSRF para pruebas con Postman
            .authorizeHttpRequests()
            .anyRequest().permitAll();  // permite todos los endpoints sin login
        return http.build();
    }
}

