package com.example.CoreBack.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;
import com.example.CoreBack.testutils.TestData;

/**
 * Tests unitarios para EventController usando Mockito sin Spring Context.
 * Versión adaptada a: el Controller delega en EventService (que publica a Rabbit)
 * y lee la apiKey del request attribute "AUTH_API_KEY" seteado por el ApiKeyFilter.
 */
@ExtendWith(MockitoExtension.class)
class EventControllerSimpleTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Mock
    private HttpServletRequest request; // para simular la apiKey del filtro

    private EventDTO validEventDTO;
    private StoredEvent storedEvent;

    private static final String API_KEY = "sk_core_usuarios_test";
    private static final String DEFAULT_ROUTING = "movie.created";

    @BeforeEach
    void setUp() {
        validEventDTO = TestData.Events.validEventDTO(); // asegurate que tenga source coherente si validás en service
        storedEvent = TestData.Events.storedEvent("test-id", "user.created");
        when(request.getAttribute("AUTH_API_KEY")).thenReturn(API_KEY);
    }

    // ============================================================
    // Tests para GET /events
    // ============================================================

    @Test
    @DisplayName("GET /events - Devuelve lista de eventos correctamente")
    void getAllEvents_ShouldReturnEventsList() {
        Map<String, Object> mockResponse = Map.of(
            "page", 0, "size", 10, "total", 1L, "events", List.of(storedEvent)
        );
        when(eventService.getAllEvents(0, 10, null, null, null)).thenReturn(mockResponse);

        ResponseEntity<?> response = eventController.getAllEvents(0, 10, null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(eventService).getAllEvents(0, 10, null, null, null);
    }

    @Test
    @DisplayName("GET /events - Con filtros aplica correctamente")
    void getAllEvents_WithFilters_ShouldApplyFilters() {
        Map<String, Object> mockResponse = Map.of(
            "page", 0, "size", 10, "total", 0L, "events", List.of()
        );
        when(eventService.getAllEvents(0, 10, "user-service", "COMPLETED", "test"))
            .thenReturn(mockResponse);

        ResponseEntity<?> response = eventController.getAllEvents(0, 10, "user-service", "COMPLETED", "test");

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
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(storedEvent));

        ResponseEntity<?> response = eventController.getEventDetail(eventId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(storedEvent.getEventId(), body.get("eventId"));
        assertEquals(storedEvent.getEventType(), body.get("type"));
        assertEquals(storedEvent.getSource(), body.get("source"));
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("GET /events/{id} - Devuelve 404 cuando no existe")
    void getEventDetail_WhenEventNotExists_ShouldReturn404() {
        Long eventId = 999L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventController.getEventDetail(eventId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(eventRepository).findById(eventId);
    }

    // ============================================================
    // Tests para POST /events/receive
    // ============================================================

    @Test
    @DisplayName("POST /events/receive - Procesa evento válido correctamente")
    void receiveEvent_WithValidEvent_ShouldProcessSuccessfully() {
        String routingKey = "user.updated";
        StoredEvent processed = TestData.Events.storedEvent("test-id", "user.updated");
        when(eventService.processIncomingEvent(validEventDTO, routingKey, API_KEY)).thenReturn(processed);

        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, routingKey, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("sent_to_queue", body.get("status"));
        assertEquals(routingKey, body.get("routingKey"));
        assertNotNull(body.get("occurredAt"));

        verify(eventService).processIncomingEvent(validEventDTO, routingKey, API_KEY);
    }

    @Test
    @DisplayName("POST /events/receive - Con routing key por defecto")
    void receiveEvent_WithoutRoutingKey_ShouldUseDefaultRoutingKey() {
        StoredEvent processed = TestData.Events.storedEvent("test-id", DEFAULT_ROUTING);
        when(eventService.processIncomingEvent(validEventDTO, DEFAULT_ROUTING, API_KEY)).thenReturn(processed);

        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, DEFAULT_ROUTING, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(DEFAULT_ROUTING, body.get("routingKey"));
        verify(eventService).processIncomingEvent(validEventDTO, DEFAULT_ROUTING, API_KEY);
    }

    @Test
    @DisplayName("POST /events/receive - Maneja errores del service")
    void receiveEvent_WithServiceError_ShouldReturnBadRequest() {
        RuntimeException err = new RuntimeException("RabbitMQ connection failed");
        when(eventService.processIncomingEvent(any(EventDTO.class), anyString(), anyString()))
            .thenThrow(err);

        ResponseEntity<?> response = eventController.receiveEvent(validEventDTO, "test.key", request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("error", body.get("status"));
        assertEquals("RabbitMQ connection failed", body.get("message"));
    }

    // ============================================================
    // Tests para GET /events/stats
    // ============================================================

    @Test
    @DisplayName("GET /events/stats - Devuelve estadísticas globales")
    void getStats_ShouldReturnGlobalStats() {
        Map<String, Object> mockStats = Map.of("total", 100, "successful", 95, "failed", 5, "pending", 0);
        when(eventService.getGlobalStats()).thenReturn(mockStats);

        ResponseEntity<?> response = eventController.getStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStats, response.getBody());
        verify(eventService).getGlobalStats();
    }

    // ============================================================
    // GET /events/evolution y /events/per-module (igual que antes)
    // ============================================================

    @Test
    @DisplayName("GET /events/evolution - Devuelve evolución de eventos")
    void getEvolution_ShouldReturnEvolution() {
        List<Map<String, Object>> mockEvolution = List.of(
            Map.of("hour", "2024-01-01T10:00:00", "count", 25),
            Map.of("hour", "2024-01-01T11:00:00", "count", 30)
        );
        when(eventService.getEvolution()).thenReturn(mockEvolution);

        ResponseEntity<?> response = eventController.getEvolution();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEvolution, response.getBody());
        verify(eventService).getEvolution();
    }

    @Test
    @DisplayName("GET /events/per-module - Devuelve eventos por módulo")
    void getEventsPerModule_ShouldReturnEventsPerModule() {
        Map<String, Long> mockModuleStats = Map.of(
            "user-service", 45L, "movie-service", 30L, "review-service", 25L
        );
        when(eventService.getEventsPerModule()).thenReturn(mockModuleStats);

        ResponseEntity<?> response = eventController.getEventsPerModule();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockModuleStats, response.getBody());
        verify(eventService).getEventsPerModule();
    }

    @Test
    @DisplayName("Verifica que no hay interacciones inesperadas")
    void verifyNoUnexpectedInteractions() {
        // nuevo test aislado, sin invocar nada
        verifyNoInteractions(eventRepository, eventService);
    }
}
