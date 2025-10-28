package com.example.CoreBack.config;

import com.example.CoreBack.security.ApiKeyFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = false)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            // si preferís habilitar CORS centralizado, usá .cors(Customizer.withDefaults())
            .cors(cors -> cors.disable())
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class) 
            .authorizeHttpRequests(authz -> authz
                
                .anyRequest().permitAll()
            )
            .build();
    }
}
