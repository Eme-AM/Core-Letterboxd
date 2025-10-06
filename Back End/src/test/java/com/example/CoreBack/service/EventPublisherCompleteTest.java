package com.example.CoreBack.service;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.testutils.EventTestDataFactory;
import com.example.CoreBack.testutils.TestEventBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para EventPublisher services.
 * Verifica la publicación de eventos a RabbitMQ con diferentes escenarios.
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherCompleteTest {

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
        EventDTO validEvent = EventTestDataFactory.createValidEventDTO();
        String routingKey = "user.created.routing";
        
        // When
        assertDoesNotThrow(() -> rabbitEventPublisher.publish(validEvent, routingKey));
        
        // Then - Verificar que se llamó el método con los parámetros correctos
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(validEvent)
        );
    }
    
    @Test
    @DisplayName("EventPublisherService debe publicar evento válido correctamente")
    void eventPublisherService_withValidEvent_shouldPublishSuccessfully() {
        // Given
        EventDTO validEvent = EventTestDataFactory.createValidEventDTO();
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
        EventDTO userEvent = EventTestDataFactory.createUserEvent("signup");
        String routingKey = "user.signup.routing";
        
        // When
        rabbitEventPublisher.publish(userEvent, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(userEvent)
        );
    }
    
    @Test
    @DisplayName("Debe publicar evento de película con routing correcto")
    void shouldPublishMovieEventWithCorrectRouting() {
        // Given
        EventDTO movieEvent = EventTestDataFactory.createMovieEvent("created");
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
        EventDTO complexEvent = EventTestDataFactory.createEventDTOWithComplexData();
        String routingKey = "rating.created.routing";
        
        // When
        rabbitEventPublisher.publish(complexEvent, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(complexEvent)
        );
    }
    
    @Test
    @DisplayName("Debe manejar routing keys específicos para diferentes módulos")
    void shouldHandleSpecificRoutingKeysForDifferentModules() {
        // Given
        EventDTO userEvent = TestEventBuilder.builder().asUserEvent("created").build();
        
        // When
        rabbitEventPublisher.publish(userEvent, RabbitConfig.ROUTING_KEY_USERS);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(RabbitConfig.ROUTING_KEY_USERS), 
            eq(userEvent)
        );
    }
    
    @Test
    @DisplayName("Debe manejar error de conexión RabbitMQ")
    void shouldHandleRabbitMQConnectionError() {
        // Given
        EventDTO event = EventTestDataFactory.createValidEventDTO();
        String routingKey = "test.routing";
        AmqpException connectionError = new AmqpException("Connection refused");
        
        doThrow(connectionError).when(rabbitTemplate)
            .convertAndSend(eq(RabbitConfig.EXCHANGE), eq(routingKey), eq(event));
        
        // When & Then
        AmqpException exception = assertThrows(AmqpException.class, 
            () -> rabbitEventPublisher.publish(event, routingKey));
        
        assertEquals("Connection refused", exception.getMessage());
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(routingKey), eq(event));
    }
    
    @Test
    @DisplayName("Debe verificar que se usa el exchange correcto")
    void shouldUseCorrectExchange() {
        // Given
        EventDTO event = EventTestDataFactory.createValidEventDTO();
        String routingKey = "test.routing";
        
        // When
        rabbitEventPublisher.publish(event, routingKey);
        
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
        EventDTO userEvent = EventTestDataFactory.createUserEvent("signup");
        EventDTO movieEvent = EventTestDataFactory.createMovieEvent("created");
        EventDTO ratingEvent = EventTestDataFactory.createRatingEvent();
        
        // When
        rabbitEventPublisher.publish(userEvent, "user.signup.routing");
        eventPublisherService.publish(movieEvent, "movie.created.routing");
        rabbitEventPublisher.publish(ratingEvent, "rating.created.routing");
        
        // Then
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, "user.signup.routing", userEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, "movie.created.routing", movieEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, "rating.created.routing", ratingEvent);
    }
    
    @Test
    @DisplayName("Debe manejar routing keys vacíos correctamente")
    void shouldHandleEmptyRoutingKeys() {
        // Given
        EventDTO event = EventTestDataFactory.createValidEventDTO();
        
        // When & Then - Empty routing key
        assertDoesNotThrow(() -> rabbitEventPublisher.publish(event, ""));
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(""), eq(event));
    }
    
    @Test
    @DisplayName("Debe manejar routing keys null correctamente")
    void shouldHandleNullRoutingKeys() {
        // Given
        EventDTO event = EventTestDataFactory.createValidEventDTO();
        
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
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(routingKey), (Object) eq(null));
    }
    
    @Test
    @DisplayName("Ambos publishers deben usar la misma interfaz correctamente")
    void bothPublishersShouldImplementSameInterface() {
        // Given
        EventDTO event = EventTestDataFactory.createValidEventDTO();
        String routingKey = "interface.test.routing";
        
        // When - Test interface implementation
        EventPublisherTest publisherInterface = rabbitEventPublisher;
        publisherInterface.publish(event, routingKey);
        
        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE), 
            eq(routingKey), 
            eq(event)
        );
    }
}
