package com.example.CoreBack.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class EventConsumerServiceTest {

    @Mock
    private EventRepository eventRepository;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private EventConsumerService eventConsumerService;

    @BeforeEach
    void setUp() {
        // Setup común para tests
    }

    @Test
    @DisplayName("Debe procesar evento válido correctamente")
    void shouldReceiveAndProcessValidEventMessage() throws Exception {
        // Given
        Map<String, Object> eventMessage = createValidEventMessage();
        String expectedJson = "{\"id\":\"test-123\"}";
        when(objectMapper.writeValueAsString(eventMessage)).thenReturn(expectedJson);
        
        StoredEvent savedEvent = new StoredEvent(
            "test-123", "user.created", "users", "application/json", 
            expectedJson, LocalDateTime.now()
        );
        when(eventRepository.save(any(StoredEvent.class))).thenReturn(savedEvent);

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(eventMessage);
        });

        // Then
        verify(objectMapper).writeValueAsString(eventMessage);
        
        ArgumentCaptor<StoredEvent> eventCaptor = ArgumentCaptor.forClass(StoredEvent.class);
        verify(eventRepository).save(eventCaptor.capture());
        
        StoredEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventId()).isEqualTo("test-123");
        assertThat(capturedEvent.getEventType()).isEqualTo("user.created");
        assertThat(capturedEvent.getSource()).isEqualTo("users");
    }

    @Test
    @DisplayName("Debe manejar mensaje vacío correctamente")
    void shouldHandleEmptyEventMessage() throws Exception {
        // Given
        Map<String, Object> emptyMessage = new HashMap<>();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(emptyMessage);
        });

        // Then
        verify(eventRepository, never()).save(any(StoredEvent.class));
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    @DisplayName("Debe manejar mensaje null correctamente")
    void shouldHandleNullEventMessage() throws Exception {
        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(null);
        });

        // Then
        verify(eventRepository, never()).save(any(StoredEvent.class));
        verify(objectMapper, never()).writeValueAsString(any());
    }

    private Map<String, Object> createValidEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "test-123");
        message.put("type", "user.created");
        message.put("source", "users");
        message.put("datacontenttype", "application/json");
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", "123");
        data.put("email", "test@example.com");
        data.put("username", "testuser");
        message.put("data", data);
        
        return message;
    }
}
