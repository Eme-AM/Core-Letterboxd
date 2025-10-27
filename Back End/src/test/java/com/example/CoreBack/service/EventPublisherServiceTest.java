package com.example.CoreBack.service;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

/**
 * Unit tests for EventPublisherService
 * 
 * Tests coverage:
 * - publish() method basic functionality
 * - RabbitMQ integration via AmqpTemplate
 */
@ExtendWith(MockitoExtension.class)
class EventPublisherServiceTest {

    @Mock
    private AmqpTemplate rabbitTemplate;

    @InjectMocks
    private EventPublisherService eventPublisherService;

    @Captor
    private ArgumentCaptor<String> exchangeCaptor;

    @Captor
    private ArgumentCaptor<String> routingKeyCaptor;

    @Captor
    private ArgumentCaptor<Object> messageCaptor;

    @Test
    @DisplayName("publish should invoke rabbitTemplate with correct parameters")
    void publish_ShouldInvokeRabbitTemplateWithCorrectParameters() {
        // Given
        Map<String, Object> message = Map.of("id", "test-123", "type", "user.created");
        String routingKey = "users.created.routing";

        // When
        eventPublisherService.publish(message, routingKey);

        // Then - Use ArgumentCaptor to avoid method ambiguity
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
        
        assertThat(routingKeyCaptor.getValue()).isEqualTo(routingKey);
        assertThat(messageCaptor.getValue()).isEqualTo(message);
    }

    @Test
    @DisplayName("publish should work with null message")
    void publish_WithNullMessage_ShouldStillCallTemplate() {
        // Given
        String routingKey = "test.routing";

        // When
        eventPublisherService.publish(null, routingKey);

        // Then
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
        
        assertThat(routingKeyCaptor.getValue()).isEqualTo(routingKey);
        assertThat(messageCaptor.getValue()).isNull();
    }

    @Test
    @DisplayName("publish should work with null routing key")
    void publish_WithNullRoutingKey_ShouldStillCallTemplate() {
        // Given
        Object message = Map.of("key", "value");

        // When
        eventPublisherService.publish(message, null);

        // Then
        verify(rabbitTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
        
        assertThat(routingKeyCaptor.getValue()).isNull();
        assertThat(messageCaptor.getValue()).isEqualTo(message);
    }
}