package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.CoreBack.service.EventPublisherService;

/**
 * Tests unitarios para TestController
 * 
 * Verifica el endpoint de prueba para eventos
 */
@WebMvcTest(TestController.class)
@Import(TestControllerTest.TestConfig.class)
class TestControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private EventPublisherService eventPublisherService;

    @Test
    @DisplayName("GET /test debe enviar evento de prueba correctamente")
    void sendTestEvent_ShouldWork() throws Exception {
        // Given - usar Object.class en lugar de EventMessage para evitar problemas de classpath en IDE
        doNothing().when(eventPublisherService).publish(any(Object.class), anyString());

        // When & Then
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Evento enviado 🚀"));

        // Verify que se publica el evento con routing key correcto
        verify(eventPublisherService).publish(any(Object.class), eq("event.pelicula"));
    }
}