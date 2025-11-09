package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TestControllerTest {

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