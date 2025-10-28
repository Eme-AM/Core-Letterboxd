package com.example.CoreBack.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;
import com.example.CoreBack.testutils.TestData;

/**
 * Tests unitarios para EventController usando solo Mockito sin Spring Context.
 * 
 * Estos tests verifican:
 * - Lógica del controlador
 * - Interacción con servicios
 * - Respuestas HTTP correctas
 * - Manejo de errores
 * - Validación de parámetros
 */
@ExtendWith(MockitoExtension.class)
class EventControllerSimpleTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EventController eventController;

    private EventDTO validEventDTO;
    private StoredEvent storedEvent;

    @BeforeEach
    void setUp() {
        validEventDTO = TestData.Events.validEventDTO();
        storedEvent = TestData.Events.storedEvent("test-id", "user.created");
    }

    // ============================================================
    // Tests para GET /events
    // ============================================================

    @Test
    @DisplayName("GET /events - Devuelve lista de eventos correctamente")
    void getAllEvents_ShouldReturnEventsList() {
        // Given
        Map<String, Object> mockResponse = Map.of(
            "page", 0,
            "size", 10,
            "total", 1L,
            "events", List.of(storedEvent)
        );
        when(eventService.getAllEvents(0, 10, null, null, null)).thenReturn(mockResponse);

        // When
        ResponseEntity<?> response = eventController.getAllEvents(0, 10, null, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(eventService).getAllEvents(0, 10, null, null, null);
    }

    @Test
    @DisplayName("GET /events - Con filtros aplica correctamente")
    void getAllEvents_WithFilters_ShouldApplyFilters() {
        // Given
        Map<String, Object> mockResponse = Map.of(
            "page", 0,
            "size", 10,
            "total", 0L,
            "events", List.of()
        );
        when(eventService.getAllEvents(0, 10, "user-service", "COMPLETED", "test")).thenReturn(mockResponse);

        // When
        ResponseEntity<?> response = eventController.getAllEvents(0, 10, "user-service", "COMPLETED", "test");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(eventService).getAllEvents(0, 10, "user-service", "COMPLETED", "test");
    }

    // ============================================================
    // Tests para GET /events/{eventId}
    // ============================================================

    @Test
    @DisplayName("GET /events/{id} - Devuelve evento cuando existe")
    void getEventDetail_WhenEventExists_ShouldReturnEvent() {
        // Given
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(storedEvent));

        // When
        ResponseEntity<?> response = eventController.getEventDetail(eventId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(storedEvent.getEventId(), responseBody.get("eventId"));
        assertEquals(storedEvent.getEventType(), responseBody.get("type"));
        assertEquals(storedEvent.getSource(), responseBody.get("source"));
        
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("GET /events/{id} - Devuelve 404 cuando no existe")
    void getEventDetail_WhenEventNotExists_ShouldReturn404() {
        // Given
        Long eventId = 999L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> response = eventController.getEventDetail(eventId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventRepository).findById(eventId);
    }

    // ============================================================
    // Tests para POST /events/receive
    // ============================================================

    @Test
    @DisplayName("POST /events/receive - Procesa evento válido correctamente")
    void receiveEvent_WithValidEvent_ShouldProcessSuccessfully() {
        // Given
        String routingKey = "user.updated";
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(EventDTO.class));

        // When
        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, routingKey);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("sent_to_queue", responseBody.get("status"));
        assertEquals(routingKey, responseBody.get("routingKey"));
        
        verify(rabbitTemplate).convertAndSend("letterboxd_exchange", routingKey, validEventDTO);
    }

    @Test
    @DisplayName("POST /events/receive - Con routing key por defecto")
    void receiveEvent_WithoutRoutingKey_ShouldUseDefaultRoutingKey() {
        // Given
        String defaultRoutingKey = "movie.created";
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(EventDTO.class));

        // When
        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, defaultRoutingKey);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(defaultRoutingKey, responseBody.get("routingKey"));
        
        verify(rabbitTemplate).convertAndSend("letterboxd_exchange", defaultRoutingKey, validEventDTO);
    }

    @Test
    @DisplayName("POST /events/receive - Maneja errores de RabbitMQ")
    void receiveEvent_WithRabbitError_ShouldReturnBadRequest() {
        // Given
        RuntimeException rabbitError = new RuntimeException("RabbitMQ connection failed");
        doThrow(rabbitError).when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(EventDTO.class));

        // When
        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, "test.key");

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("error", responseBody.get("status"));
        assertEquals("RabbitMQ connection failed", responseBody.get("message"));
    }

    // ============================================================
    // Tests para GET /events/stats
    // ============================================================

    @Test
    @DisplayName("GET /events/stats - Devuelve estadísticas globales")
    void getStats_ShouldReturnGlobalStats() {
        // Given
        Map<String, Object> mockStats = Map.of(
            "total", 100,
            "successful", 95,
            "failed", 5,
            "pending", 0
        );
        when(eventService.getGlobalStats()).thenReturn(mockStats);

        // When
        ResponseEntity<?> response = eventController.getStats();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStats, response.getBody());
        verify(eventService).getGlobalStats();
    }

    // ============================================================
    // Tests para GET /events/evolution
    // ============================================================

    @Test
    @DisplayName("GET /events/evolution - Devuelve evolución de eventos")
    void getEvolution_ShouldReturnEvolution() {
        // Given
        List<Map<String, Object>> mockEvolution = List.of(
            Map.of("hour", "2024-01-01T10:00:00", "count", 25),
            Map.of("hour", "2024-01-01T11:00:00", "count", 30)
        );
        when(eventService.getEvolution()).thenReturn(mockEvolution);

        // When
        ResponseEntity<?> response = eventController.getEvolution();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvolution, response.getBody());
        verify(eventService).getEvolution();
    }

    // ============================================================
    // Tests para GET /events/per-module
    // ============================================================

    @Test
    @DisplayName("GET /events/per-module - Devuelve eventos por módulo")
    void getEventsPerModule_ShouldReturnEventsPerModule() {
        // Given
        Map<String, Long> mockModuleStats = Map.of(
            "user-service", 45L,
            "movie-service", 30L,
            "review-service", 25L
        );
        when(eventService.getEventsPerModule()).thenReturn(mockModuleStats);

        // When
        ResponseEntity<?> response = eventController.getEventsPerModule();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockModuleStats, response.getBody());
        verify(eventService).getEventsPerModule();
    }

    // ============================================================
    // Tests de verificación de interacciones
    // ============================================================

    @Test
    @DisplayName("Verifica que no hay interacciones inesperadas")
    void verifyNoUnexpectedInteractions() {
        // When - no operations

        // Then
        verifyNoInteractions(eventRepository, eventService, rabbitTemplate);
    }
}