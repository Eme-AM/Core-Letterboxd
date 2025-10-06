package com.example.CoreBack.controllers;

import com.example.CoreBack.model.EventMessage;
import com.example.CoreBack.service.EventPublisherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    
    @MockBean
    private EventPublisherService eventPublisherService;

    @Test
    @DisplayName("GET /test debe enviar evento y retornar mensaje de éxito")
    void sendTestEvent_ShouldPublishEventAndReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(eventPublisherService).publish(any(EventMessage.class), anyString());

        // When & Then
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string("Evento enviado 🚀"));

        // Verify que se publica el evento
        verify(eventPublisherService).publish(any(EventMessage.class), eq("event.pelicula"));
    }

    @Test
    @DisplayName("GET /test debe llamar al servicio de publicación")
    void sendTestEvent_ShouldCallPublisherService() throws Exception {
        // Given
        doNothing().when(eventPublisherService).publish(any(EventMessage.class), anyString());

        // When
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());

        // Then - Verificar que el servicio fue llamado
        verify(eventPublisherService, times(1)).publish(any(EventMessage.class), eq("event.pelicula"));
    }
}