package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.service.EventPublisherService;

/**
 * Tests unitarios para TestController
 * Verifica el endpoint de prueba para eventos con outbox
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
        // Stub: no hacer nada al intentar enviar
        doNothing().when(eventPublisherService).trySend(any(StoredEvent.class));

        // When & Then
        mockMvc.perform(get("/test"))
               .andExpect(status().isOk())
               // Mejor compatible por si no incluye charset explícito
               .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
               .andExpect(content().string("Evento enviado 🚀"));

        // Capturar el StoredEvent enviado y validar la routingKey
        ArgumentCaptor<StoredEvent> captor = ArgumentCaptor.forClass(StoredEvent.class);
        verify(eventPublisherService).trySend(captor.capture());

        StoredEvent sent = captor.getValue();
        assertThat(sent).isNotNull();
        assertThat(sent.getRoutingKey()).isEqualTo("event.pelicula");
        assertThat(sent.getStatus()).isEqualTo("PENDING");   // lo setea el controller
        assertThat(sent.getPayload()).isEqualTo("{\"ping\":\"ok\"}");
        assertThat(sent.getContentType()).isEqualTo("application/json");
    }
}
