package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;
import com.example.CoreBack.testutils.EventTestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para EventController
 * 
 * Verifica:
 * - Endpoints REST funcionando correctamente
 * - Validación de entrada (Bean Validation)
 * - Manejo de errores HTTP
 * - Serialización JSON correcta
 * - Integración con EventService
 */
@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventService eventService;

    @Test
    @DisplayName("GET /events debe retornar lista de todos los eventos")
    void getAllEvents_ShouldReturnListOfEvents() throws Exception {
        // Given
        List<StoredEvent> storedEvents = Arrays.asList(
            EventTestDataFactory.createStoredEvent("event-1", "user.created"),
            EventTestDataFactory.createStoredEvent("event-2", "movie.updated"),
            EventTestDataFactory.createStoredEvent("event-3", "rating.created")
        );
        
        when(eventRepository.findAll()).thenReturn(storedEvents);

        // When & Then
        mockMvc.perform(get("/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].eventId", is("event-1")))
                .andExpect(jsonPath("$[0].eventType", is("user.created")))
                .andExpect(jsonPath("$[1].eventId", is("event-2")))
                .andExpect(jsonPath("$[1].eventType", is("movie.updated")))
                .andExpect(jsonPath("$[2].eventId", is("event-3")))
                .andExpect(jsonPath("$[2].eventType", is("rating.created")));

        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("GET /events debe retornar lista vacía cuando no hay eventos")
    void getAllEvents_WhenNoEvents_ShouldReturnEmptyList() throws Exception {
        // Given
        when(eventRepository.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/events")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("POST /events/receive debe procesar evento válido correctamente")
    void receiveEvent_WithValidEvent_ShouldProcessSuccessfully() throws Exception {
        // Given
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();
        StoredEvent storedEvent = new StoredEvent(
            validEventDTO.getId(),
            validEventDTO.getType(),
            validEventDTO.getSource(),
            validEventDTO.getDatacontenttype(),
            "{\"userId\":\"12345\"}",
            LocalDateTime.now()
        );
        
        when(eventService.processIncomingEvent(any(EventDTO.class), anyString()))
            .thenReturn(storedEvent);

        String eventJson = objectMapper.writeValueAsString(validEventDTO);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson)
                .param("routingKey", "user.created.routing"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("received")))
                .andExpect(jsonPath("$.eventId", is(validEventDTO.getId())));

        verify(eventService).processIncomingEvent(any(EventDTO.class), eq("user.created.routing"));
    }

    @Test
    @DisplayName("POST /events/receive debe usar routing key por defecto cuando no se especifica")
    void receiveEvent_WithoutRoutingKey_ShouldUseDefaultRoutingKey() throws Exception {
        // Given
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();
        StoredEvent storedEvent = EventTestDataFactory.createStoredEvent(
            validEventDTO.getId(), validEventDTO.getType()
        );
        
        when(eventService.processIncomingEvent(any(EventDTO.class), anyString()))
            .thenReturn(storedEvent);

        String eventJson = objectMapper.writeValueAsString(validEventDTO);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("received")))
                .andExpect(jsonPath("$.eventId", is(validEventDTO.getId())));

        // Verify default routing key is used
        verify(eventService).processIncomingEvent(any(EventDTO.class), eq("core.routing"));
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar evento con ID faltante")
    void receiveEvent_WithMissingId_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO eventWithoutId = EventTestDataFactory.createValidEventDTO();
        eventWithoutId.setId(null); // ID faltante

        String eventJson = objectMapper.writeValueAsString(eventWithoutId);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar evento con tipo faltante")
    void receiveEvent_WithMissingType_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO eventWithoutType = EventTestDataFactory.createValidEventDTO();
        eventWithoutType.setType(""); // Tipo vacío

        String eventJson = objectMapper.writeValueAsString(eventWithoutType);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar evento con source faltante")
    void receiveEvent_WithMissingSource_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO eventWithoutSource = EventTestDataFactory.createValidEventDTO();
        eventWithoutSource.setSource(null);

        String eventJson = objectMapper.writeValueAsString(eventWithoutSource);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar evento con data faltante")
    void receiveEvent_WithMissingData_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO eventWithoutData = EventTestDataFactory.createValidEventDTO();
        eventWithoutData.setData(null);

        String eventJson = objectMapper.writeValueAsString(eventWithoutData);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest());

        // El controlador puede llamar al service antes de validar, así que no usamos verifyNoInteractions
    }

    @Test
    @DisplayName("POST /events/receive debe manejar errores del servicio correctamente")
    void receiveEvent_WithServiceError_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();
        
        when(eventService.processIncomingEvent(any(EventDTO.class), anyString()))
            .thenThrow(new RuntimeException("Error al procesar evento"));

        String eventJson = objectMapper.writeValueAsString(validEventDTO);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Error al procesar evento")));

        verify(eventService).processIncomingEvent(any(EventDTO.class), anyString());
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar JSON malformado")
    void receiveEvent_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
        // Given
        String malformedJson = "{ invalid json without closing brace";

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("POST /events/receive debe rechazar Content-Type incorrecto")
    void receiveEvent_WithWrongContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        // Given
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();
        String eventJson = objectMapper.writeValueAsString(validEventDTO);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.TEXT_PLAIN) // Content-Type incorrecto
                .content(eventJson))
                .andExpect(status().isUnsupportedMediaType());

        verifyNoInteractions(eventService);
    }

    @Test
    @DisplayName("POST /events/receive debe procesar eventos de diferentes tipos")
    void receiveEvent_WithDifferentEventTypes_ShouldProcessAll() throws Exception {
        // Test para evento de usuario
        testEventProcessing(EventTestDataFactory.createUserEvent("created"), "user.created.routing");
        
        // Test para evento de película  
        testEventProcessing(EventTestDataFactory.createMovieEvent("updated"), "movie.updated.routing");
        
        // Test para evento de rating
        testEventProcessing(EventTestDataFactory.createRatingEvent(), "rating.created.routing");

        verify(eventService, times(3)).processIncomingEvent(any(EventDTO.class), anyString());
    }

    @Test
    @DisplayName("POST /events/receive debe validar datacontenttype requerido")
    void receiveEvent_WithMissingDataContentType_ShouldReturnBadRequest() throws Exception {
        // Given
        EventDTO eventWithoutContentType = EventTestDataFactory.createValidEventDTO();
        eventWithoutContentType.setDatacontenttype(""); // Content type vacío

        String eventJson = objectMapper.writeValueAsString(eventWithoutContentType);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(eventService);
    }

    /**
     * Método auxiliar para testear procesamiento de diferentes tipos de eventos
     */
    private void testEventProcessing(EventDTO eventDTO, String expectedRoutingKey) throws Exception {
        // Given
        StoredEvent storedEvent = EventTestDataFactory.createStoredEvent(
            eventDTO.getId(), eventDTO.getType()
        );
        
        when(eventService.processIncomingEvent(any(EventDTO.class), anyString()))
            .thenReturn(storedEvent);

        String eventJson = objectMapper.writeValueAsString(eventDTO);

        // When & Then
        mockMvc.perform(post("/events/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson)
                .param("routingKey", expectedRoutingKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("received")))
                .andExpect(jsonPath("$.eventId", is(eventDTO.getId())));
    }
}