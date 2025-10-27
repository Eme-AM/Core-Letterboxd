package com.example.CoreBack.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.testutils.TestData;

/**
 * Tests de integración para EventRepository
 * 
 * Verifica:
 * - Operaciones CRUD funcionando correctamente
 * - Queries JPA automáticas
 * - Persistencia en H2 database
 * - Relaciones de entidad correctas
 */
@DataJpaTest
@ActiveProfiles("test")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("save() debe persistir StoredEvent correctamente")
    void save_ShouldPersistStoredEventCorrectly() {
        // Given
        StoredEvent storedEvent = TestData.Events.storedEvent(
            "test-event-123", "user.created"
        );

        // When
        StoredEvent savedEvent = eventRepository.save(storedEvent);

        // Then
        assertThat(savedEvent.getId()).isNotNull(); // ID generado automáticamente
        assertThat(savedEvent.getEventId()).isEqualTo("test-event-123");
        assertThat(savedEvent.getEventType()).isEqualTo("user.created");

        // Verificar en database
        entityManager.flush();
        StoredEvent foundEvent = entityManager.find(StoredEvent.class, savedEvent.getId());
        assertThat(foundEvent).isNotNull();
        assertThat(foundEvent.getEventId()).isEqualTo("test-event-123");
    }

    @Test
    @DisplayName("findAll() debe retornar todos los eventos")
    void findAll_ShouldReturnAllEvents() {
        // Given
        StoredEvent event1 = TestData.Events.storedEvent("event-1", "user.created");
        StoredEvent event2 = TestData.Events.storedEvent("event-2", "movie.updated");
        StoredEvent event3 = TestData.Events.storedEvent("event-3", "rating.created");
        
        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        // When
        List<StoredEvent> allEvents = eventRepository.findAll();

        // Then
        assertThat(allEvents).hasSize(3);
        assertThat(allEvents).extracting(StoredEvent::getEventId)
            .containsExactlyInAnyOrder("event-1", "event-2", "event-3");
        assertThat(allEvents).extracting(StoredEvent::getEventType)
            .containsExactlyInAnyOrder("user.created", "movie.updated", "rating.created");
    }

    @Test
    @DisplayName("findById() debe retornar evento específico")
    void findById_ShouldReturnSpecificEvent() {
        // Given
        StoredEvent storedEvent = TestData.Events.storedEvent(
            "specific-event", "user.login"
        );
        StoredEvent savedEvent = eventRepository.save(storedEvent);

        // When
        Optional<StoredEvent> foundEvent = eventRepository.findById(savedEvent.getId());

        // Then
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getEventId()).isEqualTo("specific-event");
        assertThat(foundEvent.get().getEventType()).isEqualTo("user.login");
    }

    @Test
    @DisplayName("findById() debe retornar empty para ID inexistente")
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<StoredEvent> foundEvent = eventRepository.findById(99999L);

        // Then
        assertThat(foundEvent).isEmpty();
    }

    @Test
    @DisplayName("delete() debe eliminar evento correctamente")
    void delete_ShouldRemoveEventCorrectly() {
        // Given
        StoredEvent storedEvent = TestData.Events.storedEvent(
            "event-to-delete", "user.deleted"
        );
        StoredEvent savedEvent = eventRepository.save(storedEvent);
        Long eventId = savedEvent.getId();

        // When
        eventRepository.delete(savedEvent);

        // Then
        Optional<StoredEvent> deletedEvent = eventRepository.findById(eventId);
        assertThat(deletedEvent).isEmpty();

        List<StoredEvent> allEvents = eventRepository.findAll();
        assertThat(allEvents).doesNotContain(savedEvent);
    }

    @Test
    @DisplayName("count() debe retornar número correcto de eventos")
    void count_ShouldReturnCorrectNumberOfEvents() {
        // Given
        assertThat(eventRepository.count()).isZero(); // Database limpia

        StoredEvent event1 = TestData.Events.storedEvent("count-1", "user.created");
        StoredEvent event2 = TestData.Events.storedEvent("count-2", "user.updated");
        
        eventRepository.save(event1);
        eventRepository.save(event2);

        // When
        long eventCount = eventRepository.count();

        // Then
        assertThat(eventCount).isEqualTo(2L);
    }

    @Test
    @DisplayName("existsById() debe verificar existencia correctamente")
    void existsById_ShouldVerifyExistenceCorrectly() {
        // Given
        StoredEvent storedEvent = TestData.Events.storedEvent(
            "exists-check", "user.verified"
        );
        StoredEvent savedEvent = eventRepository.save(storedEvent);

        // When & Then
        assertThat(eventRepository.existsById(savedEvent.getId())).isTrue();
        assertThat(eventRepository.existsById(99999L)).isFalse();
    }

    @Test
    @DisplayName("Debe manejar payload JSON largo correctamente")
    void repository_ShouldHandleLongJsonPayloadCorrectly() {
        // Given
        StringBuilder largePayload = new StringBuilder("{\"data\":[");
        for (int i = 0; i < 100; i++) {
            largePayload.append("{\"id\":").append(i).append(",\"value\":\"item-").append(i).append("\"}");
            if (i < 99) largePayload.append(",");
        }
        largePayload.append("]}");

        StoredEvent eventWithLargePayload = new StoredEvent(
            "data.bulk.import", "import-service",
            "application/json", largePayload.toString(), LocalDateTime.now()
        );
        eventWithLargePayload.setEventId("large-payload-event");

        // When
        StoredEvent savedEvent = eventRepository.save(eventWithLargePayload);

        // Then
        assertThat(savedEvent.getId()).isNotNull();
        
        Optional<StoredEvent> retrievedEvent = eventRepository.findById(savedEvent.getId());
        assertThat(retrievedEvent).isPresent();
        assertThat(retrievedEvent.get().getPayload()).hasSize(largePayload.length());
        assertThat(retrievedEvent.get().getPayload()).contains("\"id\":50");
    }

    @Test
    @DisplayName("Debe preservar precisión de fechas")
    void repository_ShouldPreserveDateTimePrecision() {
        // Given - H2 preserva hasta microsegundos, no nanosegundos
        LocalDateTime preciseDateTime = LocalDateTime.of(2024, 4, 10, 16, 45, 23, 987654000);
        
        StoredEvent eventWithPreciseDate = new StoredEvent(
            "time.precision.test", "test-service",
            "application/json", "{\"timestamp\":\"test\"}", preciseDateTime
        );
        eventWithPreciseDate.setEventId("precise-date-event");

        // When
        StoredEvent savedEvent = eventRepository.save(eventWithPreciseDate);
        entityManager.flush();
        entityManager.clear(); // Clear cache to force reload from DB

        // Then
        Optional<StoredEvent> retrievedEvent = eventRepository.findById(savedEvent.getId());
        assertThat(retrievedEvent).isPresent();
        assertThat(retrievedEvent.get().getOccurredAt()).isEqualTo(preciseDateTime);
    }

    @Test
    @DisplayName("Debe manejar caracteres especiales en campos de texto")
    void repository_ShouldHandleSpecialCharactersInTextFields() {
        // Given
        String specialCharsPayload = """
            {
                "title": "Película con áéíóú ñ €",
                "description": "Description with emoji 🎬🍿 and symbols @#$%",
                "unicode": "Unicode test: \\u00A9 \\u00AE \\u2122"
            }
            """;

        StoredEvent eventWithSpecialChars = new StoredEvent(
            "movie.created", "movies-service",
            "application/json", specialCharsPayload, LocalDateTime.now()
        );
        eventWithSpecialChars.setEventId("special-chars-event");

        // When
        StoredEvent savedEvent = eventRepository.save(eventWithSpecialChars);

        // Then
        Optional<StoredEvent> retrievedEvent = eventRepository.findById(savedEvent.getId());
        assertThat(retrievedEvent).isPresent();
        assertThat(retrievedEvent.get().getPayload()).contains("áéíóú ñ €");
        assertThat(retrievedEvent.get().getPayload()).contains("🎬🍿");
        assertThat(retrievedEvent.get().getPayload()).contains("@#$%");
    }

    @Test
    @DisplayName("Transacciones deben funcionar correctamente")
    void repository_ShouldHandleTransactionsCorrectly() {
        // Given
        StoredEvent event1 = TestData.Events.storedEvent("tx-event-1", "user.created");
        StoredEvent event2 = TestData.Events.storedEvent("tx-event-2", "user.updated");

        // When - Save multiple events in same transaction
        eventRepository.save(event1);
        eventRepository.save(event2);
        
        // Then
        List<StoredEvent> allEvents = eventRepository.findAll();
        assertThat(allEvents).hasSize(2);
        assertThat(allEvents).extracting(StoredEvent::getEventId)
            .containsExactlyInAnyOrder("tx-event-1", "tx-event-2");
    }
}