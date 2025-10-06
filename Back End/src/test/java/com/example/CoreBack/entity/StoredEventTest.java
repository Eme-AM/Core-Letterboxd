package com.example.CoreBack.entity;

import com.example.CoreBack.testutils.EventTestDataFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para StoredEvent
 * 
 * Verifica:
 * - Constructores funcionando correctamente
 * - Getters y Setters
 * - Estructura de entidad JPA
 * - Manejo de datos correctos
 */
class StoredEventTest {

    @Test
    @DisplayName("Constructor con parámetros debe crear StoredEvent correctamente")
    void constructorWithParameters_ShouldCreateStoredEventCorrectly() {
        // Given
        String eventId = "event-12345";
        String eventType = "user.created";
        String source = "user-service";
        String contentType = "application/json";
        String payload = "{\"userId\":\"12345\",\"email\":\"test@example.com\"}";
        LocalDateTime occurredAt = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

        // When
        StoredEvent storedEvent = new StoredEvent(eventId, eventType, source, contentType, payload, occurredAt);

        // Then
        assertThat(storedEvent.getEventId()).isEqualTo(eventId);
        assertThat(storedEvent.getEventType()).isEqualTo(eventType);
        assertThat(storedEvent.getSource()).isEqualTo(source);
        assertThat(storedEvent.getContentType()).isEqualTo(contentType);
        assertThat(storedEvent.getPayload()).isEqualTo(payload);
        assertThat(storedEvent.getOccurredAt()).isEqualTo(occurredAt);
        assertThat(storedEvent.getId()).isNull(); // No asignado hasta persistir
    }

    @Test
    @DisplayName("Constructor por defecto debe crear StoredEvent vacío")
    void defaultConstructor_ShouldCreateEmptyStoredEvent() {
        // When
        StoredEvent storedEvent = new StoredEvent();

        // Then
        assertThat(storedEvent.getId()).isNull();
        assertThat(storedEvent.getEventId()).isNull();
        assertThat(storedEvent.getEventType()).isNull();
        assertThat(storedEvent.getSource()).isNull();
        assertThat(storedEvent.getContentType()).isNull();
        assertThat(storedEvent.getPayload()).isNull();
        assertThat(storedEvent.getOccurredAt()).isNull();
    }

    @Test
    @DisplayName("Setters deben actualizar valores correctamente")
    void setters_ShouldUpdateValuesCorrectly() {
        // Given
        StoredEvent storedEvent = new StoredEvent();
        LocalDateTime testDate = LocalDateTime.of(2024, 2, 20, 15, 45, 30);

        // When
        storedEvent.setEventId("updated-event-id");
        storedEvent.setEventType("movie.updated");
        storedEvent.setSource("movie-service");
        storedEvent.setContentType("application/json");
        storedEvent.setPayload("{\"movieId\":\"67890\",\"title\":\"Test Movie\"}");
        storedEvent.setOccurredAt(testDate);

        // Then
        assertThat(storedEvent.getEventId()).isEqualTo("updated-event-id");
        assertThat(storedEvent.getEventType()).isEqualTo("movie.updated");
        assertThat(storedEvent.getSource()).isEqualTo("movie-service");
        assertThat(storedEvent.getContentType()).isEqualTo("application/json");
        assertThat(storedEvent.getPayload()).isEqualTo("{\"movieId\":\"67890\",\"title\":\"Test Movie\"}");
        assertThat(storedEvent.getOccurredAt()).isEqualTo(testDate);
    }

    @Test
    @DisplayName("Debe aceptar payload JSON complejo")
    void storedEvent_ShouldAcceptComplexJsonPayload() {
        // Given
        String complexPayload = """
            {
                "userId": "12345",
                "profile": {
                    "name": "John Doe",
                    "preferences": {
                        "genres": ["action", "comedy"],
                        "notifications": true
                    }
                },
                "metadata": {
                    "timestamp": "2024-01-15T10:30:00",
                    "version": "1.0"
                }
            }
            """;

        // When
        StoredEvent storedEvent = new StoredEvent(
            "complex-event-123", "user.profile.updated", "user-service", 
            "application/json", complexPayload, LocalDateTime.now()
        );

        // Then
        assertThat(storedEvent.getPayload()).isEqualTo(complexPayload);
        assertThat(storedEvent.getPayload()).contains("userId");
        assertThat(storedEvent.getPayload()).contains("profile");
        assertThat(storedEvent.getPayload()).contains("preferences");
        assertThat(storedEvent.getPayload()).contains("metadata");
    }

    @Test
    @DisplayName("Debe aceptar diferentes tipos de eventos")
    void storedEvent_ShouldAcceptDifferentEventTypes() {
        // Given & When & Then
        
        // Evento de usuario
        StoredEvent userEvent = EventTestDataFactory.createStoredEvent("user-1", "user.created");
        assertThat(userEvent.getEventType()).isEqualTo("user.created");

        // Evento de película
        StoredEvent movieEvent = EventTestDataFactory.createStoredEvent("movie-1", "movie.created");
        assertThat(movieEvent.getEventType()).isEqualTo("movie.created");

        // Evento de rating
        StoredEvent ratingEvent = EventTestDataFactory.createStoredEvent("rating-1", "rating.created");
        assertThat(ratingEvent.getEventType()).isEqualTo("rating.created");

        // Evento social
        StoredEvent socialEvent = EventTestDataFactory.createStoredEvent("social-1", "social.follow");
        assertThat(socialEvent.getEventType()).isEqualTo("social.follow");
    }

    @Test
    @DisplayName("Debe manejar valores nulos sin errores")
    void storedEvent_ShouldHandleNullValuesWithoutErrors() {
        // Given & When
        StoredEvent storedEvent = new StoredEvent(null, null, null, null, null, null);

        // Then
        assertThat(storedEvent.getEventId()).isNull();
        assertThat(storedEvent.getEventType()).isNull();
        assertThat(storedEvent.getSource()).isNull();
        assertThat(storedEvent.getContentType()).isNull();
        assertThat(storedEvent.getPayload()).isNull();
        assertThat(storedEvent.getOccurredAt()).isNull();
    }

    @Test
    @DisplayName("Debe aceptar cadenas vacías")
    void storedEvent_ShouldAcceptEmptyStrings() {
        // Given
        LocalDateTime testDate = LocalDateTime.now();

        // When
        StoredEvent storedEvent = new StoredEvent("", "", "", "", "", testDate);

        // Then
        assertThat(storedEvent.getEventId()).isEmpty();
        assertThat(storedEvent.getEventType()).isEmpty();
        assertThat(storedEvent.getSource()).isEmpty();
        assertThat(storedEvent.getContentType()).isEmpty();
        assertThat(storedEvent.getPayload()).isEmpty();
        assertThat(storedEvent.getOccurredAt()).isEqualTo(testDate);
    }

    @Test
    @DisplayName("Debe aceptar diferentes content types")
    void storedEvent_ShouldAcceptDifferentContentTypes() {
        // Given
        LocalDateTime testDate = LocalDateTime.now();

        // When & Then
        
        // JSON
        StoredEvent jsonEvent = new StoredEvent(
            "json-1", "test.event", "test-service", "application/json", "{}", testDate
        );
        assertThat(jsonEvent.getContentType()).isEqualTo("application/json");

        // XML
        StoredEvent xmlEvent = new StoredEvent(
            "xml-1", "test.event", "test-service", "application/xml", "<data/>", testDate
        );
        assertThat(xmlEvent.getContentType()).isEqualTo("application/xml");

        // Plain Text
        StoredEvent textEvent = new StoredEvent(
            "text-1", "test.event", "test-service", "text/plain", "plain text data", testDate
        );
        assertThat(textEvent.getContentType()).isEqualTo("text/plain");
    }

    @Test
    @DisplayName("Debe preservar precision de LocalDateTime")
    void storedEvent_ShouldPreserveDateTimePrecision() {
        // Given
        LocalDateTime preciseDate = LocalDateTime.of(2024, 3, 15, 14, 25, 37, 123456789);

        // When
        StoredEvent storedEvent = new StoredEvent(
            "precise-event", "test.event", "test-service", 
            "application/json", "{}", preciseDate
        );

        // Then
        assertThat(storedEvent.getOccurredAt()).isEqualTo(preciseDate);
        assertThat(storedEvent.getOccurredAt().getNano()).isEqualTo(123456789);
    }

    @Test
    @DisplayName("Debe crear eventos con diferentes fuentes")
    void storedEvent_ShouldCreateEventsWithDifferentSources() {
        // Given
        LocalDateTime testDate = LocalDateTime.now();
        
        // When & Then
        StoredEvent userServiceEvent = new StoredEvent(
            "event-1", "user.created", "user-service", "application/json", "{}", testDate
        );
        assertThat(userServiceEvent.getSource()).isEqualTo("user-service");

        StoredEvent movieServiceEvent = new StoredEvent(
            "event-2", "movie.created", "movie-service", "application/json", "{}", testDate
        );
        assertThat(movieServiceEvent.getSource()).isEqualTo("movie-service");

        StoredEvent externalApiEvent = new StoredEvent(
            "event-3", "data.imported", "external-api", "application/json", "{}", testDate
        );
        assertThat(externalApiEvent.getSource()).isEqualTo("external-api");
    }
}