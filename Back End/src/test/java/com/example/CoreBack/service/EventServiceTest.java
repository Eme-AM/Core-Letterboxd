package com.example.CoreBack.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.security.KeyStore;
import com.example.CoreBack.testutils.TestData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests unitarios para EventService (con autorización por API Key).
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private EventPublisherService publisherService;
    @Mock private ObjectMapper objectMapper;
    @Mock private KeyStore keyStore;

    private EventService eventService;

    private static final String API_KEY = "sk_core_usuarios_test";
    private static final String SOURCE_OK = "/usuarios/api";

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, publisherService, objectMapper, keyStore);
    }

    @Test
    @DisplayName("processIncomingEvent procesa evento válido con autorización OK")
    void processIncomingEvent_withValidEvent_shouldProcessSuccessfully() throws Exception {
        EventDTO validEventDTO = TestData.Events.validEventDTO(); // asegúrate que source = "/usuarios/api"
        String routingKey = "usuarios.usuario.created";
        String payloadJson = "{\"userId\":\"12345\",\"email\":\"user@example.com\"}";

        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, routingKey)).thenReturn(true);
        when(keyStore.sourceOf(API_KEY)).thenReturn(Optional.of(SOURCE_OK));

        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);

        StoredEvent result = eventService.processIncomingEvent(validEventDTO, routingKey, API_KEY);

        assertNotNull(result);
        assertEquals(validEventDTO.getType(), result.getEventType());
        assertEquals(validEventDTO.getSource(), result.getSource());
        assertEquals(validEventDTO.getDatacontenttype(), result.getContentType());
        assertEquals(payloadJson, result.getPayload());
        assertNotNull(result.getOccurredAt());

        verify(objectMapper).writeValueAsString(validEventDTO.getData());
        verify(publisherService).publish(validEventDTO, routingKey);
    }

    @Test
    @DisplayName("processIncomingEvent rechaza si la apiKey es inválida")
    void processIncomingEvent_withInvalidApiKey_shouldThrowSecurityException() throws Exception {
        EventDTO dto = TestData.Events.validEventDTO();
        when(keyStore.isValidKey(API_KEY)).thenReturn(false);

        SecurityException ex = assertThrows(SecurityException.class, () ->
            eventService.processIncomingEvent(dto, "usuarios.usuario.created", API_KEY)
        );
        assertTrue(ex.getMessage().contains("Missing or invalid X-API-KEY"));
        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("processIncomingEvent rechaza si el routingKey no está autorizado")
    void processIncomingEvent_withForbiddenRoutingKey_shouldThrowSecurityException() throws Exception {
        EventDTO dto = TestData.Events.validEventDTO();
        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, "movies.movie.created")).thenReturn(false);

        SecurityException ex = assertThrows(SecurityException.class, () ->
            eventService.processIncomingEvent(dto, "movies.movie.created", API_KEY)
        );
        assertTrue(ex.getMessage().contains("no autorizada para el routingKey"));
        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("processIncomingEvent rechaza si el source no coincide con la key")
    void processIncomingEvent_withMismatchedSource_shouldThrowSecurityException() throws Exception {
        EventDTO dto = TestData.Builder.event().withSource("/movies/api").build();

        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, "usuarios.usuario.created")).thenReturn(true);
        when(keyStore.sourceOf(API_KEY)).thenReturn(Optional.of(SOURCE_OK));

        SecurityException ex = assertThrows(SecurityException.class, () ->
            eventService.processIncomingEvent(dto, "usuarios.usuario.created", API_KEY)
        );
        assertTrue(ex.getMessage().contains("API Key no autorizada para el source"));
        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("processIncomingEvent usa fecha actual cuando sysDate es nulo")
    void processIncomingEvent_withNullSysDate_shouldUseCurrentTime() throws Exception {
        EventDTO dto = TestData.Builder.event().withSysDate(null).withSource(SOURCE_OK).build();
        String routingKey = "usuarios.usuario.updated";

        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, routingKey)).thenReturn(true);
        when(keyStore.sourceOf(API_KEY)).thenReturn(Optional.of(SOURCE_OK));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"test\":\"data\"}");

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        StoredEvent result = eventService.processIncomingEvent(dto, routingKey, API_KEY);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(result.getOccurredAt());
        assertTrue(result.getOccurredAt().isAfter(before));
        assertTrue(result.getOccurredAt().isBefore(after));
        verify(publisherService).publish(dto, routingKey);
    }

    @Test
    @DisplayName("processIncomingEvent maneja error de serialización JSON")
    void processIncomingEvent_withJsonSerializationError_shouldThrowRuntimeException() throws Exception {
        EventDTO dto = TestData.Builder.event().withSource(SOURCE_OK).build();
        String routingKey = "usuarios.usuario.created";
        JsonProcessingException jsonEx = new JsonProcessingException("JSON error") {};

        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, routingKey)).thenReturn(true);
        when(keyStore.sourceOf(API_KEY)).thenReturn(Optional.of(SOURCE_OK));
        when(objectMapper.writeValueAsString(any())).thenThrow(jsonEx);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            eventService.processIncomingEvent(dto, routingKey, API_KEY)
        );
        assertEquals("Error procesando evento", ex.getMessage());
        assertEquals(jsonEx, ex.getCause());
        verifyNoInteractions(publisherService);
    }

    @Test
    @DisplayName("processIncomingEvent maneja error del publisher")
    void processIncomingEvent_withPublisherError_shouldThrowRuntimeException() throws Exception {
        EventDTO dto = TestData.Builder.event().withSource(SOURCE_OK).build();
        String routingKey = "usuarios.usuario.created";
        RuntimeException pubEx = new RuntimeException("RabbitMQ connection error");

        when(keyStore.isValidKey(API_KEY)).thenReturn(true);
        when(keyStore.isTypeAllowed(API_KEY, routingKey)).thenReturn(true);
        when(keyStore.sourceOf(API_KEY)).thenReturn(Optional.of(SOURCE_OK));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        doThrow(pubEx).when(publisherService).publish(any(), any());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            eventService.processIncomingEvent(dto, routingKey, API_KEY)
        );
        assertEquals("Error procesando evento", ex.getMessage());
        assertEquals(pubEx, ex.getCause());

        verify(objectMapper).writeValueAsString(dto.getData());
        verify(publisherService).publish(dto, routingKey);
    }

    // --- El resto de tests (getAllEvents, getGlobalStats, getEvolution, getEventsPerModule) quedan igual ---

    @Test
    @DisplayName("getAllEvents debe filtrar y paginar eventos correctamente")
    void getAllEvents_withFilters_shouldReturnFilteredPaginatedResults() {
        StoredEvent e1 = createStoredEvent("user.created", "usuarios", "Delivered");
        StoredEvent e2 = createStoredEvent("movie.created", "peliculas", "Failed");
        StoredEvent e3 = createStoredEvent("review.created", "reviews", "Delivered");

        List<StoredEvent> mockEvents = List.of(e1, e2, e3);
        Page<StoredEvent> mockPage = new PageImpl<>(mockEvents, Pageable.ofSize(10), 3);

        when(eventRepository.findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class)))
            .thenReturn(mockPage);

        Map<String, Object> result = eventService.getAllEvents(0, 10, "usuarios", "delivered", null);

        assertNotNull(result);
        assertEquals(0, result.get("page"));
        assertEquals(10, result.get("size"));
        assertEquals(3L, result.get("total"));
        assertEquals(mockEvents, result.get("events"));

        verify(eventRepository).findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class));
    }

    @Test
    @DisplayName("getGlobalStats debe calcular estadísticas correctamente")
    void getGlobalStats_shouldCalculateStatsCorrectly() {
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(15);
        LocalDateTime lastMonth = thisMonth.minusMonths(1);

        List<StoredEvent> mockEvents = List.of(
            createStoredEventWithDate("event1", "source1", "Delivered", thisMonth),
            createStoredEventWithDate("event2", "source2", "Failed", thisMonth),
            createStoredEventWithDate("event3", "source3", "Delivered", lastMonth),
            createStoredEventWithDate("event4", "source4", "InQueue", thisMonth)
        );

        when(eventRepository.findAll()).thenReturn(mockEvents);

        Map<String, Object> result = eventService.getGlobalStats();

        assertNotNull(result);
        assertTrue(result.containsKey("thisMonth"));
        assertTrue(result.containsKey("lastMonth"));
        assertTrue(result.containsKey("totalEvents"));
        assertTrue(result.containsKey("delivered"));
        assertTrue(result.containsKey("failed"));
        assertTrue(result.containsKey("inQueue"));

        verify(eventRepository).findAll();
    }

    // Helpers
    private StoredEvent createStoredEvent(String type, String source, String status) {
        return createStoredEventWithDate(type, source, status, LocalDateTime.now());
    }
    private StoredEvent createStoredEventWithDate(String type, String source, String status, LocalDateTime occurredAt) {
        StoredEvent ev = new StoredEvent();
        ev.setEventType(type);
        ev.setSource(source);
        ev.setStatus(status);
        ev.setOccurredAt(occurredAt);
        ev.setPayload("{}");
        ev.setContentType("application/json");
        return ev;
    }
}
