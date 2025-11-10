package com.example.CoreBack.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.AmqpTemplate;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;

@ExtendWith(MockitoExtension.class)
class EventPublisherServiceTest {

    @Mock
    private AmqpTemplate rabbitTemplate;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventPublisherService eventPublisherService;

    @Test
    @DisplayName("trySend debe enviar un evento PENDING correctamente")
    void trySend_ShouldSendPendingEventSuccessfully() {
        // Given
        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey("users.created.routing");
        ev.setPayload("{\"id\":\"test-123\",\"type\":\"user.created\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.save(any(StoredEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        eventPublisherService.trySend(ev);

        // Then
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.EXCHANGE),
            eq(ev.getRoutingKey()),
            eq(ev.getPayload()),
            any()
        );

        verify(eventRepository).save(ev);
        assertThat(ev.getStatus()).isEqualTo("DELIVERED");
        assertThat(ev.getDeliveredAt()).isNotNull();
    }

    @Test
    @DisplayName("trySend debe reprogramar reintento si RabbitMQ está caído")
    void trySend_ShouldScheduleRetryWhenBrokerDown() {
        // Given
        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey("users.created.routing");
        ev.setPayload("{\"id\":\"test-123\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.save(any(StoredEvent.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new AmqpConnectException(new RuntimeException("Connection refused")))
            .when(rabbitTemplate)
            .convertAndSend(anyString(), anyString(), anyString(), any());

        // When
        eventPublisherService.trySend(ev);

        // Then
        verify(eventRepository, atLeastOnce()).save(ev);
        assertThat(ev.getStatus()).isEqualTo("PENDING");
        assertThat(ev.getNextAttemptAt()).isAfter(LocalDateTime.now());
        assertThat(ev.getError()).contains("Broker down");
    }

    @Test
    @DisplayName("resendPending debe reenviar eventos pendientes")
    void resendPending_ShouldResendPendingEvents() {
        // Given
        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey("users.created.routing");
        ev.setPayload("{\"id\":\"test-456\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now().minusSeconds(10));
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.findTop100ByStatusInAndNextAttemptAtBeforeOrderByNextAttemptAtAsc(
            anyList(), any(LocalDateTime.class))
        ).thenReturn(List.of(ev));
        when(eventRepository.save(any(StoredEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        eventPublisherService.resendPending();

        // Then
        verify(rabbitTemplate).convertAndSend(eq(RabbitConfig.EXCHANGE), eq(ev.getRoutingKey()), eq(ev.getPayload()), any());
        verify(eventRepository, atLeastOnce()).save(ev);
        assertThat(ev.getStatus()).isEqualTo("DELIVERED");
    }
}
