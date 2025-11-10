package com.example.CoreBack.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.testutils.TestData;

@ExtendWith(MockitoExtension.class)
class RabbitEventPublisherTest {

    @Mock
    private AmqpTemplate rabbitTemplate;

    @Mock
    private EventRepository eventRepository; // requerido por EventPublisherService

    private RabbitEventPublisher rabbitEventPublisher;
    private EventPublisherService eventPublisherService;

    @BeforeEach
    void setUp() {
        // Publisher “simple” (usa 3 args)
        rabbitEventPublisher = new RabbitEventPublisher(rabbitTemplate);
        // Service de tu app con outbox (usa 4 args por MessagePostProcessor)
        eventPublisherService = new EventPublisherService(rabbitTemplate, eventRepository);
    }

    // ---------- Tests para RabbitEventPublisher (3 parámetros) ----------

    @Test
    @DisplayName("RabbitEventPublisher debe publicar evento válido correctamente")
    void rabbitEventPublisher_withValidEvent_shouldPublishSuccessfully() {
        EventDTO validEvent = TestData.Events.validEventDTO();
        String routingKey = "user.created.routing";

        assertDoesNotThrow(() -> rabbitEventPublisher.publish(validEvent, routingKey));

        // Tipamos todos los matchers para evitar ambigüedad
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(routingKey),
            ArgumentMatchers.<Object>eq(validEvent)
        );
    }

    @Test
    @DisplayName("Debe publicar evento de usuario con routing correcto (publisher simple)")
    void shouldPublishUserEventWithCorrectRouting_simplePublisher() {
        EventDTO userEvent = TestData.Events.userEvent("signup");
        String routingKey = "user.signup.routing";

        rabbitEventPublisher.publish(userEvent, routingKey);

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(routingKey),
            ArgumentMatchers.<Object>eq(userEvent)
        );
    }

    @Test
    @DisplayName("Debe publicar evento de rating con datos complejos (publisher simple)")
    void shouldPublishRatingEventWithComplexData_simplePublisher() {
        EventDTO complexEvent = TestData.Events.complexEvent();
        String routingKey = "rating.created.routing";

        rabbitEventPublisher.publish(complexEvent, routingKey);

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(routingKey),
            ArgumentMatchers.<Object>eq(complexEvent)
        );
    }

    @Test
    @DisplayName("Should handle specific routing keys for different modules (publisher simple)")
    void shouldHandleSpecificRoutingKeysForDifferentModules_simplePublisher() {
        EventDTO userEvent = TestData.Builder.event()
                .asUserCreated(123L, "test@example.com", "testuser")
                .build();

        rabbitEventPublisher.publish(userEvent, RabbitConfig.RK_USER);

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(RabbitConfig.RK_USER),
            ArgumentMatchers.<Object>eq(userEvent)
        );
    }

    @Test
    @DisplayName("Debe manejar error de conexión RabbitMQ (publisher simple)")
    void shouldHandleRabbitMQConnectionError_simplePublisher() {
        EventDTO event = TestData.Events.validEventDTO();
        String routingKey = "test.routing";
        AmqpException connectionError = new AmqpException("Connection refused");

        doThrow(connectionError)
            .when(rabbitTemplate)
            .convertAndSend(
                ArgumentMatchers.<String>eq("letterboxd_exchange"),
                ArgumentMatchers.<String>eq(routingKey),
                ArgumentMatchers.<Object>eq(event)
            );

        AmqpException exception = assertThrows(AmqpException.class,
            () -> rabbitEventPublisher.publish(event, routingKey));

        assertEquals("Connection refused", exception.getMessage());
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(routingKey),
            ArgumentMatchers.<Object>eq(event)
        );
    }

    @Test
    @DisplayName("Debe publicar diferentes tipos de eventos correctamente (publisher simple)")
    void shouldPublishDifferentEventTypesCorrectly_simplePublisher() {
        EventDTO userEvent = TestData.Events.userEvent("user123");
        EventDTO ratingEvent = TestData.Events.ratingEvent("user123", "movie456", 4.5);

        rabbitEventPublisher.publish(userEvent, "user.signup.routing");
        rabbitEventPublisher.publish(ratingEvent, "rating.created.routing");

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq("user.signup.routing"),
            ArgumentMatchers.<Object>eq(userEvent)
        );
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq("rating.created.routing"),
            ArgumentMatchers.<Object>eq(ratingEvent)
        );
    }

    @Test
    @DisplayName("Debe manejar routing keys vacíos correctamente (publisher simple)")
    void shouldHandleEmptyRoutingKeys_simplePublisher() {
        EventDTO event = TestData.Events.validEventDTO();

        assertDoesNotThrow(() -> rabbitEventPublisher.publish(event, ""));
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(""),
            ArgumentMatchers.<Object>eq(event)
        );
    }

    @Test
    @DisplayName("Debe publicar evento nulo sin errores (publisher simple)")
    void shouldPublishNullEventWithoutErrors_simplePublisher() {
        String routingKey = "test.routing";

        assertDoesNotThrow(() -> rabbitEventPublisher.publish(null, routingKey));
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(routingKey),
            ArgumentMatchers.<Object>isNull()
        );
    }

    // ---------- Tests para EventPublisherService (outbox + 4 parámetros) ----------

    @Test
    @DisplayName("EventPublisherService.trySend debe publicar usando EXCHANGE y MessagePostProcessor")
    void eventPublisherService_trySend_shouldPublishWithMessagePostProcessor() {
        // Given: evento PENDING en outbox
        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey("movie.created.routing");
        ev.setPayload("{\"id\":\"movie-1\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.save(ArgumentMatchers.<StoredEvent>any()))
            .thenAnswer(inv -> inv.getArgument(0));

        // When
        eventPublisherService.trySend(ev);

        // Then: verifica la sobrecarga de 4 args con MessagePostProcessor
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq(RabbitConfig.EXCHANGE),
            ArgumentMatchers.<String>eq(ev.getRoutingKey()),
            ArgumentMatchers.<Object>eq(ev.getPayload()),
            ArgumentMatchers.<MessagePostProcessor>any()
        );
        // Guardado como DELIVERED
        verify(eventRepository).save(ev);
    }

    @Test
    @DisplayName("EventPublisherService.trySend debe tolerar routingKey null (usa 4 args)")
    void eventPublisherService_trySend_shouldHandleNullRoutingKey() {
        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey(null); // caso null
        ev.setPayload("{\"id\":\"x\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.save(ArgumentMatchers.<StoredEvent>any()))
            .thenAnswer(inv -> inv.getArgument(0));

        eventPublisherService.trySend(ev);

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq(RabbitConfig.EXCHANGE),
            ArgumentMatchers.<String>isNull(),
            ArgumentMatchers.<Object>eq(ev.getPayload()),
            ArgumentMatchers.<MessagePostProcessor>any()
        );
        verify(eventRepository).save(ev);
    }

    @Test
    @DisplayName("Ambos publishers deben manejar la publicación correctamente (simple=3 args, service=4 args)")
    void bothPublishersShouldHandlePublishingCorrectly_mixed() {
        EventDTO dto = TestData.Events.validEventDTO();
        String rk = "comparison.test.routing";

        StoredEvent ev = new StoredEvent();
        ev.setRoutingKey(rk);
        ev.setPayload("{\"id\":\"cmp\"}");
        ev.setContentType("application/json");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        when(eventRepository.save(ArgumentMatchers.<StoredEvent>any()))
            .thenAnswer(inv -> inv.getArgument(0));

        // simple publisher (3 args)
        rabbitEventPublisher.publish(dto, rk);
        // service (4 args)
        eventPublisherService.trySend(ev);

        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq("letterboxd_exchange"),
            ArgumentMatchers.<String>eq(rk),
            ArgumentMatchers.<Object>eq(dto)
        );
        verify(rabbitTemplate).convertAndSend(
            ArgumentMatchers.<String>eq(RabbitConfig.EXCHANGE),
            ArgumentMatchers.<String>eq(rk),
            ArgumentMatchers.<Object>eq(ev.getPayload()),
            ArgumentMatchers.<MessagePostProcessor>any()
        );
    }
}
