package com.example.CoreBack.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

/**
 * Tests unitarios para EventConsumerService
 * 
 * Verifica el comportamiento de los métodos @RabbitListener existentes:
 * - Procesamiento de eventos en diferentes colas
 * - Manejo de mensajes válidos 
 * - Verificación de guardado en base de datos
 * - Validación de duplicados
 */
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
    void shouldReceiveAndProcessAllEventsMessage() throws Exception {
        // Given
        Map<String, Object> eventMessage = createValidEventMessage();
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(eventMessage)).thenReturn("{\"id\":\"test-123\"}");
        
        StoredEvent savedEvent = new StoredEvent(
            "test-123", "user.created", "users", "application/json", 
            "{\"id\":\"test-123\"}", LocalDateTime.now()
        );
        when(eventRepository.save(any(StoredEvent.class))).thenReturn(savedEvent);

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(eventMessage);
        });

        // Then
        verify(eventRepository).findAll();
        verify(objectMapper).writeValueAsString(eventMessage);
        verify(eventRepository).save(any(StoredEvent.class));
    }

    @Test
    void shouldReceiveUserEvents() {
        // Given
        Map<String, Object> userEventMessage = createUserEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveUserEvents(userEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
        // pero confirma que no lanza excepciones
    }

    @Test
    void shouldReceiveMovieEvents() {
        // Given
        Map<String, Object> movieEventMessage = createMovieEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveMovieEvents(movieEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
    }

    @Test
    void shouldReceiveRatingEvents() {
        // Given
        Map<String, Object> ratingEventMessage = createRatingEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveRatingEvents(ratingEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
    }

    @Test
    void shouldReceiveSocialEvents() {
        // Given
        Map<String, Object> socialEventMessage = createSocialEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveSocialEvents(socialEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
    }

    @Test
    void shouldReceiveAnalyticsEvents() {
        // Given
        Map<String, Object> analyticsEventMessage = createAnalyticsEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAnalyticsEvents(analyticsEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
    }

    @Test
    void shouldReceiveRecommendationsEvents() {
        // Given
        Map<String, Object> recommendationsEventMessage = createRecommendationsEventMessage();

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveRecommendationsEvents(recommendationsEventMessage);
        });

        // Then - Método solo imprime, no hay verificaciones específicas
    }

    @Test
    void shouldHandleDuplicateEventsInAllQueue() throws Exception {
        // Given
        Map<String, Object> eventMessage = createValidEventMessage();
        StoredEvent existingEvent = new StoredEvent(
            "test-123", "user.created", "users", "application/json", 
            "{}", LocalDateTime.now()
        );
        
        when(eventRepository.findAll()).thenReturn(Collections.singletonList(existingEvent));

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(eventMessage);
        });

        // Then
        verify(eventRepository).findAll();
        verify(eventRepository, never()).save(any(StoredEvent.class));
        verify(objectMapper, never()).writeValueAsString(any());
    }

    @Test
    void shouldHandleObjectMapperExceptionInAllQueue() throws Exception {
        // Given
        Map<String, Object> eventMessage = createValidEventMessage();
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(eventMessage))
            .thenThrow(new RuntimeException("JSON serialization error"));

        // When
        assertDoesNotThrow(() -> {
            eventConsumerService.receiveAllEvents(eventMessage);
        });

        // Then
        verify(eventRepository).findAll();
        verify(objectMapper).writeValueAsString(eventMessage);
        verify(eventRepository, never()).save(any(StoredEvent.class));
    }

    @Test
    void shouldCreateStoredEventWithCorrectData() throws Exception {
        // Given
        Map<String, Object> eventMessage = Map.of(
            "id", "event-456",
            "type", "movie.updated",
            "source", "movies-service",
            "data", Map.of("movieId", 123, "title", "Test Movie")
        );
        
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        when(objectMapper.writeValueAsString(eventMessage)).thenReturn("{\"movieId\":123}");

        ArgumentCaptor<StoredEvent> eventCaptor = ArgumentCaptor.forClass(StoredEvent.class);
        when(eventRepository.save(eventCaptor.capture())).thenReturn(null);

        // When
        eventConsumerService.receiveAllEvents(eventMessage);

        // Then
        verify(eventRepository).save(any(StoredEvent.class));
        
        StoredEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventId()).isEqualTo("event-456");
        assertThat(capturedEvent.getEventType()).isEqualTo("movie.updated");
        assertThat(capturedEvent.getSource()).isEqualTo("movies-service");
        assertThat(capturedEvent.getContentType()).isEqualTo("application/json");
        assertThat(capturedEvent.getPayload()).isEqualTo("{\"movieId\":123}");
        assertThat(capturedEvent.getOccurredAt()).isNotNull();
    }

    /**
     * Métodos auxiliares para crear datos de test
     */
    private Map<String, Object> createValidEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "test-123");
        message.put("type", "user.created");
        message.put("source", "users");
        message.put("data", Map.of("userId", 123, "email", "test@example.com"));
        return message;
    }

    private Map<String, Object> createUserEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "user-456");
        message.put("type", "user.signup");
        message.put("source", "user-service");
        message.put("data", Map.of("userId", 456, "username", "newuser"));
        return message;
    }

    private Map<String, Object> createMovieEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "movie-789");
        message.put("type", "movie.created");
        message.put("source", "movie-service");
        message.put("data", Map.of("movieId", 789, "title", "Test Movie"));
        return message;
    }

    private Map<String, Object> createRatingEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "rating-101");
        message.put("type", "rating.created");
        message.put("source", "rating-service");
        message.put("data", Map.of("ratingId", 101, "score", 4.5));
        return message;
    }

    private Map<String, Object> createSocialEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "social-202");
        message.put("type", "social.follow");
        message.put("source", "social-service");
        message.put("data", Map.of("followerId", 1, "followeeId", 2));
        return message;
    }

    private Map<String, Object> createAnalyticsEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "analytics-303");
        message.put("type", "analytics.view");
        message.put("source", "analytics-service");
        message.put("data", Map.of("pageView", "movie-detail", "userId", 303));
        return message;
    }

    private Map<String, Object> createRecommendationsEventMessage() {
        Map<String, Object> message = new HashMap<>();
        message.put("id", "recommendations-404");
        message.put("type", "recommendations.generated");
        message.put("source", "recommendations-service");
        message.put("data", Map.of("userId", 404, "movieIds", java.util.List.of(1, 2, 3)));
        return message;
    }
}
