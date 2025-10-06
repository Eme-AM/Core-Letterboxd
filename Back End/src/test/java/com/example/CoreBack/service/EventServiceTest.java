package com.example.CoreBack.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.testutils.EventTestDataFactory;
import com.example.CoreBack.testutils.TestEventBuilder;
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
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();
        String routingKey = "user.created.routing";
        String expectedPayloadJson = "{\"userId\":\"12345\",\"email\":\"user@example.com\"}";
        
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedPayloadJson);
        
        // When
        StoredEvent result = eventService.processIncomingEvent(validEventDTO, routingKey);
        
        // Then
        assertNotNull(result);
        assertEquals(validEventDTO.getId(), result.getEventId());
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
        EventDTO eventWithNullDate = TestEventBuilder.builder()
            .withDate(null)
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
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
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
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
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
}