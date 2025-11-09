package com.example.CoreBack.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.testutils.TestData;

/**
 * Tests unitarios para RabbitEventPublisher service.
 * Verifica la publicación de eventos a RabbitMQ con diferentes escenarios.
 */
@ExtendWith(MockitoExtension.class)
class RabbitEventPublisherTest {

    @Mock
    private AmqpTemplate rabbitTemplate;
    
    private RabbitEventPublisher rabbitEventPublisher;
    private EventPublisherService eventPublisherService;
    
    @BeforeEach 
    void setUp() {
        rabbitEventPublisher = new RabbitEventPublisher(rabbitTemplate);
        eventPublisherService = new EventPublisherService(rabbitTemplate);
    }
    
    @Test
    @DisplayName("RabbitEventPublisher debe publicar evento válido correctamente")
    void rabbitEventPublisher_withValidEvent_shouldPublishSuccessfully() {
        // Given
        EventDTO validEvent = TestData.Events.validEventDTO();
        String routingKey = "user.created.routing";
        
        // When
        assertDoesNotThrow(() -> rabbitEventPublisher.publish(validEvent, routingKey));
        
        // Then - Verificar que se llamó el método con los parámetros correctos
        verify(rabbitTemplate).convertAndSend(eq("letterboxd_exchange"), eq(routingKey), eq(validEvent));
    }
    
    @Test
    @DisplayName("EventPublisherService debe publicar evento válido correctamente")
    void eventPublisherService_withValidEvent_shouldPublishSuccessfully() {
        // Given
        EventDTO validEvent = TestData.Events.validEventDTO();
        String routingKey = "user.created.routing";
        
        // When
        assertDoesNotThrow(() -> eventPublisherService.publish(validEvent, routingKey));
        
        // Then - Verificar que se llamó el método con los parámetros correctos
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(validEvent)
        );
    }
    
    @Test
    @DisplayName("Debe publicar evento de usuario con routing correcto")
    void shouldPublishUserEventWithCorrectRouting() {
        // Given
        EventDTO userEvent = TestData.Events.userEvent("signup");
        String routingKey = "user.signup.routing";
        
        // When
        rabbitEventPublisher.publish(userEvent, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq("letterboxd_exchange"), 
            eq(routingKey), 
            eq(userEvent)
        );
    }
    
    @Test
    @DisplayName("Debe publicar evento de película con routing correcto")
    void shouldPublishMovieEventWithCorrectRouting() {
        // Given
        EventDTO movieEvent = TestData.Events.movieEvent("created");
        String routingKey = "movie.created.routing";
        
        // When
        eventPublisherService.publish(movieEvent, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(movieEvent)
        );
    }
    
    @Test
    @DisplayName("Debe publicar evento de rating con datos complejos")
    void shouldPublishRatingEventWithComplexData() {
        // Given
        EventDTO complexEvent = TestData.Events.complexEvent();
        String routingKey = "rating.created.routing";
        
        // When
        rabbitEventPublisher.publish(complexEvent, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq("letterboxd_exchange"), 
            eq(routingKey), 
            eq(complexEvent)
        );
    }
    
    @Test
    @DisplayName("Should handle specific routing keys for different modules")
    void shouldHandleSpecificRoutingKeysForDifferentModules() {
        // Given
        EventDTO userEvent = TestData.Builder.event().asUserCreated(123L, "test@example.com", "testuser").build();
        
        // When
        rabbitEventPublisher.publish(userEvent, RabbitConfig.RK_USER);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq("letterboxd_exchange"), 
            eq(RabbitConfig.RK_USER), 
            eq(userEvent)
        );
    }
    
    @Test
    @DisplayName("Debe manejar error de conexión RabbitMQ")
    void shouldHandleRabbitMQConnectionError() {
        // Given
        EventDTO event = TestData.Events.validEventDTO();
        String routingKey = "test.routing";
        AmqpException connectionError = new AmqpException("Connection refused");
        
        doThrow(connectionError).when(rabbitTemplate)
            .convertAndSend(eq("letterboxd_exchange"), eq(routingKey), eq(event));
        
        // When & Then
        AmqpException exception = assertThrows(AmqpException.class, 
            () -> rabbitEventPublisher.publish(event, routingKey));
        
        assertEquals("Connection refused", exception.getMessage());
        verify(rabbitTemplate).convertAndSend(eq("letterboxd_exchange"), eq(routingKey), eq(event));
    }
    
    @Test
    @DisplayName("Debe verificar que EventPublisherService usa el exchange correcto")
    void shouldUseCorrectExchange() {
        // Given
        EventDTO event = TestData.Events.validEventDTO();
        String routingKey = "test.routing";
        
        // When
        eventPublisherService.publish(event, routingKey);
        
        // Then - Verificar que usa el exchange correcto
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE),
            eq(routingKey),
            eq(event)
        );
    }
    
    @Test
    @DisplayName("Debe publicar diferentes tipos de eventos correctamente")
    void shouldPublishDifferentEventTypesCorrectly() {
        // Given
        EventDTO userEvent = TestData.Events.userEvent("user123");
        EventDTO movieEvent = TestData.Events.movieEvent("movie456");
        EventDTO ratingEvent = TestData.Events.ratingEvent("user123", "movie456", 4.5);
        
        // When
        rabbitEventPublisher.publish(userEvent, "user.signup.routing");
        eventPublisherService.publish(movieEvent, "movie.created.routing");
        rabbitEventPublisher.publish(ratingEvent, "rating.created.routing");
        
        // Then
        verify(rabbitTemplate).convertAndSend("letterboxd_exchange", "user.signup.routing", userEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, "movie.created.routing", movieEvent);
        verify(rabbitTemplate).convertAndSend("letterboxd_exchange", "rating.created.routing", ratingEvent);
    }
    
    @Test
    @DisplayName("Debe manejar routing keys vacíos correctamente")
    void shouldHandleEmptyRoutingKeys() {
        // Given
        EventDTO event = TestData.Events.validEventDTO();
        
        // When & Then - Empty routing key
        assertDoesNotThrow(() -> rabbitEventPublisher.publish(event, ""));
        verify(rabbitTemplate).convertAndSend(eq("letterboxd_exchange"), eq(""), eq(event));
    }
    
    @Test
    @DisplayName("Debe manejar routing keys null correctamente")
    void shouldHandleNullRoutingKeys() {
        // Given
        EventDTO event = TestData.Events.validEventDTO();
        
        // When & Then - Null routing key
        assertDoesNotThrow(() -> eventPublisherService.publish(event, null));
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(null), eq(event));
    }
    
    @Test
    @DisplayName("Debe publicar evento nulo sin errores")
    void shouldPublishNullEventWithoutErrors() {
        // Given
        String routingKey = "test.routing";
        
        // When & Then
        assertDoesNotThrow(() -> rabbitEventPublisher.publish(null, routingKey));
        verify(rabbitTemplate).convertAndSend(eq("letterboxd_exchange"), eq(routingKey), (Object) eq(null));
    }
    
    @Test
    @DisplayName("Ambos publishers deben manejar la publicación correctamente")
    void bothPublishersShouldHandlePublishingCorrectly() {
        // Given
        EventDTO event = TestData.Events.validEventDTO();
        String routingKey = "comparison.test.routing";
        
        // When - Test both implementations
        rabbitEventPublisher.publish(event, routingKey);
        eventPublisherService.publish(event, routingKey);
        
        // Then - Verify rabbitTemplate was called twice (once per publisher) with same exchange
        verify(rabbitTemplate, times(2)).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(routingKey), eq(event));
    }
}
