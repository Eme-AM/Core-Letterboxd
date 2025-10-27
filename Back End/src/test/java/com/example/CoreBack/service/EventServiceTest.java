package com.example.CoreBack.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.testutils.TestData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests unitarios para EventService.
 * Verifica el procesamiento de eventos, validación, serialización y publicación.
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private EventPublisherService publisherService;
    
    @Mock
    private ObjectMapper objectMapper;
    
    private EventService eventService;
    
    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, publisherService, objectMapper);
    }
    
    @Test
    @DisplayName("processIncomingEvent debe procesar evento válido correctamente")
    void processIncomingEvent_withValidEvent_shouldProcessSuccessfully() throws Exception {
        // Given
        EventDTO validEventDTO = TestData.Events.validEventDTO();
        String routingKey = "user.created.routing";
        String expectedPayloadJson = "{\"userId\":\"12345\",\"email\":\"user@example.com\"}";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedPayloadJson);
        
        // When
        StoredEvent result = eventService.processIncomingEvent(validEventDTO, routingKey);
        
        // Then
        assertNotNull(result);
        // EventDTO no tiene getId en producción, verificar otros campos
        assertEquals(validEventDTO.getType(), result.getEventType());
        assertEquals(validEventDTO.getSource(), result.getSource());
        assertEquals(validEventDTO.getDatacontenttype(), result.getContentType());
        assertEquals(expectedPayloadJson, result.getPayload());
        assertNotNull(result.getOccurredAt());
        
        // Verify interactions
        verify(objectMapper).writeValueAsString(validEventDTO.getData());
        verify(publisherService).publish(validEventDTO, routingKey);
    }
    
    @Test
    @DisplayName("processIncomingEvent debe usar fecha actual cuando sysDate es nulo")
    void processIncomingEvent_withNullSysDate_shouldUseCurrentTime() throws Exception {
        // Given
        EventDTO eventWithNullDate = TestData.Builder.event()
            .withSysDate(null)
            .build();
        String routingKey = "test.routing";
        String payloadJson = "{\"test\":\"data\"}";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(payloadJson);
        
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        
        // When
        StoredEvent result = eventService.processIncomingEvent(eventWithNullDate, routingKey);
        
        LocalDateTime afterTest = LocalDateTime.now().plusSeconds(1);
        
        // Then
        assertNotNull(result.getOccurredAt());
        assertTrue(result.getOccurredAt().isAfter(beforeTest));
        assertTrue(result.getOccurredAt().isBefore(afterTest));
        
        verify(publisherService).publish(eventWithNullDate, routingKey);
    }
    
    @Test
    @DisplayName("processIncomingEvent debe manejar error de serialización JSON")
    void processIncomingEvent_withJsonSerializationError_shouldThrowRuntimeException() throws Exception {
        // Given
        EventDTO eventDTO = TestData.Events.validEventDTO();
        String routingKey = "test.routing";
        JsonProcessingException jsonException = new JsonProcessingException("JSON error") {};
        
        when(objectMapper.writeValueAsString(any())).thenThrow(jsonException);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> eventService.processIncomingEvent(eventDTO, routingKey));
        
        assertEquals("Error procesando evento", exception.getMessage());
        assertEquals(jsonException, exception.getCause());
        
        // Verificar que no se intentó publicar el evento tras el error
        verify(objectMapper).writeValueAsString(eventDTO.getData());
        verifyNoInteractions(publisherService);
    }
    
    @Test
    @DisplayName("processIncomingEvent debe manejar error de publicación")
    void processIncomingEvent_withPublisherError_shouldThrowRuntimeException() throws Exception {
        // Given
        EventDTO eventDTO = TestData.Events.validEventDTO();
        String routingKey = "test.routing";
        RuntimeException publisherException = new RuntimeException("RabbitMQ connection error");
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        doThrow(publisherException).when(publisherService).publish(any(), any());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> eventService.processIncomingEvent(eventDTO, routingKey));
        
        assertEquals("Error procesando evento", exception.getMessage());
        assertEquals(publisherException, exception.getCause());
        
        // Verificar que se intentó serializar antes del error de publicación
        verify(objectMapper).writeValueAsString(eventDTO.getData());
        verify(publisherService).publish(eventDTO, routingKey);
    }

    @Test
    @DisplayName("getAllEvents debe filtrar y paginar eventos correctamente")
    void getAllEvents_withFilters_shouldReturnFilteredPaginatedResults() {
        // Given
        StoredEvent event1 = createStoredEvent("user.created", "usuarios", "Delivered");
        StoredEvent event2 = createStoredEvent("movie.created", "peliculas", "Failed");
        StoredEvent event3 = createStoredEvent("review.created", "reviews", "Delivered");
        
        List<StoredEvent> mockEvents = List.of(event1, event2, event3);
        Page<StoredEvent> mockPage = new PageImpl<>(mockEvents, Pageable.ofSize(10), 3);
        
        when(eventRepository.findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class)))
            .thenReturn(mockPage);
        
        // When
        Map<String, Object> result = eventService.getAllEvents(0, 10, "usuarios", "delivered", null);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.get("page"));
        assertEquals(10, result.get("size"));
        assertEquals(3L, result.get("total"));
        assertEquals(mockEvents, result.get("events"));
        
        verify(eventRepository).findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class));
    }

    @Test
    @DisplayName("getAllEvents sin filtros debe retornar todos los eventos")
    void getAllEvents_withoutFilters_shouldReturnAllEvents() {
        // Given
        List<StoredEvent> mockEvents = List.of(
            createStoredEvent("event1", "source1", "Delivered"),
            createStoredEvent("event2", "source2", "Failed")
        );
        Page<StoredEvent> mockPage = new PageImpl<>(mockEvents, Pageable.ofSize(5), 2);
        
        when(eventRepository.findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class)))
            .thenReturn(mockPage);
        
        // When
        Map<String, Object> result = eventService.getAllEvents(0, 5, null, null, null);
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.get("page"));
        assertEquals(5, result.get("size"));
        assertEquals(2L, result.get("total"));
        assertEquals(mockEvents, result.get("events"));
    }

    @Test
    @DisplayName("getGlobalStats debe calcular estadísticas correctamente")
    void getGlobalStats_shouldCalculateStatsCorrectly() {
        // Given
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(15);
        LocalDateTime lastMonth = thisMonth.minusMonths(1);
        
        List<StoredEvent> mockEvents = List.of(
            createStoredEventWithDate("event1", "source1", "Delivered", thisMonth),
            createStoredEventWithDate("event2", "source2", "Failed", thisMonth),
            createStoredEventWithDate("event3", "source3", "Delivered", lastMonth),
            createStoredEventWithDate("event4", "source4", "InQueue", thisMonth)
        );
        
        when(eventRepository.findAll()).thenReturn(mockEvents);
        
        // When
        Map<String, Object> result = eventService.getGlobalStats();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("thisMonth"));
        assertTrue(result.containsKey("lastMonth"));
        assertTrue(result.containsKey("totalEvents"));
        assertTrue(result.containsKey("delivered"));
        assertTrue(result.containsKey("failed"));
        assertTrue(result.containsKey("inQueue"));
        
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getEvolution debe retornar evolución de últimas 24 horas")
    void getEvolution_shouldReturnLast24HoursEvolution() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoHoursAgo = now.minusHours(2);
        LocalDateTime oneHourAgo = now.minusHours(1);
        
        List<StoredEvent> mockEvents = List.of(
            createStoredEventWithDate("event1", "source1", "Delivered", twoHoursAgo),
            createStoredEventWithDate("event2", "source2", "Delivered", oneHourAgo),
            createStoredEventWithDate("event3", "source3", "Failed", oneHourAgo)
        );
        
        when(eventRepository.findAll()).thenReturn(mockEvents);
        
        // When
        List<Map<String, Object>> result = eventService.getEvolution();
        
        // Then
        assertNotNull(result);
        assertEquals(24, result.size()); // 24 horas
        
        // Verificar que cada entrada tiene hour y count
        for (Map<String, Object> hourData : result) {
            assertTrue(hourData.containsKey("hour"));
            assertTrue(hourData.containsKey("count"));
        }
        
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getEventsPerModule debe agrupar eventos por módulo")
    void getEventsPerModule_shouldGroupEventsByModule() {
        // Given
        List<StoredEvent> mockEvents = List.of(
            createStoredEvent("user.created", "usuarios", "Delivered"),
            createStoredEvent("movie.created", "peliculas", "Delivered"),
            createStoredEvent("review.created", "reviews", "Failed"),
            createStoredEvent("user.updated", "usuarios", "Delivered"),
            createStoredEvent("social.follow", "social", "Delivered")
        );
        
        when(eventRepository.findAll()).thenReturn(mockEvents);
        
        // When
        Map<String, Long> result = eventService.getEventsPerModule();
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("usuarios"));
        assertTrue(result.containsKey("peliculas"));
        assertTrue(result.containsKey("reviews"));
        assertTrue(result.containsKey("social"));
        assertTrue(result.containsKey("discovery"));
        
        // Verificar que no hay módulos null/vacíos
        assertFalse(result.containsKey(null));
        
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("processIncomingEvent debe usar fecha actual cuando sysDate es muy futura")
    void processIncomingEvent_withFutureSysDate_shouldUseCurrentTime() throws Exception {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusHours(1); // Más de 5 minutos en el futuro
        EventDTO eventWithFutureDate = TestData.Builder.anEvent()
            .withSysDate(futureDate)
            .build();
        String routingKey = "test.routing";
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        
        // When
        StoredEvent result = eventService.processIncomingEvent(eventWithFutureDate, routingKey);
        
        LocalDateTime afterTest = LocalDateTime.now().plusSeconds(1);
        
        // Then
        assertNotNull(result.getOccurredAt());
        assertTrue(result.getOccurredAt().isAfter(beforeTest));
        assertTrue(result.getOccurredAt().isBefore(afterTest));
        verify(publisherService).publish(eventWithFutureDate, routingKey);
    }

    @Test
    @DisplayName("processIncomingEvent debe usar fecha actual cuando sysDate es muy antigua")
    void processIncomingEvent_withOldSysDate_shouldUseCurrentTime() throws Exception {
        // Given
        LocalDateTime oldDate = LocalDateTime.now().minusDays(2); // Más de 1 día en el pasado
        EventDTO eventWithOldDate = TestData.Builder.anEvent()
            .withSysDate(oldDate)
            .build();
        String routingKey = "test.routing";
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        
        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        
        // When
        StoredEvent result = eventService.processIncomingEvent(eventWithOldDate, routingKey);
        
        LocalDateTime afterTest = LocalDateTime.now().plusSeconds(1);
        
        // Then
        assertNotNull(result.getOccurredAt());
        assertTrue(result.getOccurredAt().isAfter(beforeTest));
        assertTrue(result.getOccurredAt().isBefore(afterTest));
        verify(publisherService).publish(eventWithOldDate, routingKey);
    }

    @Test
    @DisplayName("getAllEvents debe manejar parámetros vacíos correctamente")
    void getAllEvents_withBlankParameters_shouldIgnoreFilters() {
        // Given
        List<StoredEvent> mockEvents = List.of(createStoredEvent("test", "test", "Delivered"));
        Page<StoredEvent> mockPage = new PageImpl<>(mockEvents, Pageable.ofSize(10), 1);
        
        when(eventRepository.findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class)))
            .thenReturn(mockPage);
        
        // When - probando con strings vacíos
        Map<String, Object> result = eventService.getAllEvents(0, 10, "", "", "");
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(eventRepository).findAll(ArgumentMatchers.<Specification<StoredEvent>>any(), any(Pageable.class));
    }

    @Test
    @DisplayName("getEventsPerModule debe manejar fuentes desconocidas")
    void getEventsPerModule_withUnknownSources_shouldFilterOut() {
        // Given
        List<StoredEvent> mockEvents = List.of(
            createStoredEvent("unknown.event", "unknown-service", "Delivered"),
            createStoredEvent("user.created", "user-service", "Delivered"), // Should match "usuarios"
            createStoredEvent("random.event", "random-api", "Failed")
        );
        
        when(eventRepository.findAll()).thenReturn(mockEvents);
        
        // When
        Map<String, Long> result = eventService.getEventsPerModule();
        
        // Then
        assertNotNull(result);
        // Verificar que todos los módulos están presentes (incluso con 0)
        assertEquals(5, result.size());
        assertTrue(result.containsKey("usuarios"));
        assertTrue(result.containsKey("social"));
        assertTrue(result.containsKey("reviews"));
        assertTrue(result.containsKey("peliculas"));
        assertTrue(result.containsKey("discovery"));
        
        verify(eventRepository).findAll();
    }

    // Métodos auxiliares para crear objetos de prueba
    private StoredEvent createStoredEvent(String type, String source, String status) {
        return createStoredEventWithDate(type, source, status, LocalDateTime.now());
    }
    
    private StoredEvent createStoredEventWithDate(String type, String source, String status, LocalDateTime occurredAt) {
        StoredEvent event = new StoredEvent();
        event.setEventType(type);
        event.setSource(source);
        event.setStatus(status);
        event.setOccurredAt(occurredAt);
        event.setPayload("{}");
        event.setContentType("application/json");
        return event;
    }
}