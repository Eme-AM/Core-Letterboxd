package com.example.CoreBack.testutils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;

/**
 * Tests unitarios para las utilidades de testing.
 * 
 * Verifica que EventTestDataFactory, TestEventBuilder y StoredEventTestDataFactory
 * generen datos válidos para usar en tests.
 */
class TestDataTest {

    @Test
    void eventTestDataFactory_shouldCreateValidEventDTO() {
        // When
        EventDTO event = TestData.Events.validEventDTO();
        
        // Then
        assertNotNull(event);
        // EventDTO no tiene getId en producción
        assertNotNull(event.getType());
        assertNotNull(event.getSource());
        assertNotNull(event.getDatacontenttype());
        assertNotNull(event.getSysDate());
        assertNotNull(event.getData());
        assertFalse(event.getData().isEmpty());
    }

    @Test
    void eventTestDataFactory_shouldCreateUserCreatedEvent() {
        // Given
        Long userId = 123L;
        String email = "test@example.com";
        String username = "testuser";
        
        // When
        EventDTO event = TestData.Events.createUserCreatedEvent(userId, email, username);
        
        // Then
        assertNotNull(event);
        assertEquals("user.created", event.getType());
        assertEquals("/users/signup", event.getSource());
        assertEquals(userId, event.getData().get("userId"));
        assertEquals(email, event.getData().get("email"));
        assertEquals(username, event.getData().get("username"));
    }

    @Test
    void eventTestDataFactory_shouldCreateInvalidEventDTO() {
        // When
        EventDTO event = TestData.Events.invalidEvent();
        
        // Then
        assertNotNull(event);
        // EventDTO no tiene getId en producción
        assertNull(event.getType());
        assertNull(event.getSysDate());
        assertNull(event.getData());
    }

    @Test
    void testEventBuilder_shouldCreateEventWithFluentAPI() {
        // Given
        String customId = "test-id-123";
        LocalDateTime customTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        // When
        EventDTO event = TestData.Builder.anEvent()
            .withId(customId)
            .withType("custom.event")
            .withSource("/custom/endpoint")
            .withSysDate(customTime)
            .withDataField("key1", "value1")
            .withDataField("key2", 42)
            .build();
        
        // Then
        assertNotNull(event);
        // EventDTO no tiene getId en producción
        assertEquals("custom.event", event.getType());
        assertEquals("/custom/endpoint", event.getSource());
        assertEquals(customTime, event.getSysDate());
        assertEquals("value1", event.getData().get("key1"));
        assertEquals(42, event.getData().get("key2"));
    }

    @Test
    void testEventBuilder_shouldCreateUserCreatedEvent() {
        // Given
        Long userId = 456L;
        String email = "builder@example.com";
        String username = "builderuser";
        
        // When
        EventDTO event = TestData.Builder.anEvent()
            .asUserCreated(userId, email, username)
            .build();
        
        // Then
        assertNotNull(event);
        assertEquals("user.created", event.getType());
        assertEquals("/users/signup", event.getSource());
        assertEquals(userId, event.getData().get("userId"));
        assertEquals(email, event.getData().get("email"));
        assertEquals(username, event.getData().get("username"));
    }

    @Test
    void testEventBuilder_shouldCreateInvalidEvent() {
        // When
        EventDTO event = TestData.Builder.anEvent()
            .asInvalid()
            .build();
        
        // Then
        assertNotNull(event);
        // EventDTO no tiene getId en producción
        assertNull(event.getType());
        assertNull(event.getSysDate());
        assertNull(event.getData());
    }

    @Test
    void storedEventTestDataFactory_shouldCreateValidStoredEvent() {
        // When
        StoredEvent event = TestData.Events.validStoredEvent();
        
        // Then
        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertNotNull(event.getEventType());
        assertNotNull(event.getSource());
        assertNotNull(event.getContentType());
        assertNotNull(event.getPayload());
        assertNotNull(event.getOccurredAt());
    }

    @Test
    void storedEventTestDataFactory_shouldCreateUserCreatedStoredEvent() {
        // Given
        Long userId = 789L;
        String email = "stored@example.com";
        String username = "storeduser";
        
        // When
        StoredEvent event = TestData.Events.userCreatedStored(userId, email, username);
        
        // Then
        assertNotNull(event);
        assertEquals("user.created", event.getEventType());
        assertEquals("/users/signup", event.getSource());
        assertTrue(event.getPayload().contains(userId.toString()));
        assertTrue(event.getPayload().contains(email));
        assertTrue(event.getPayload().contains(username));
    }

    @Test
    void storedEventTestDataFactory_shouldCreateMultipleStoredEvents() {
        // Given
        int count = 3;
        
        // When
        StoredEvent[] events = TestData.Events.multipleStoredEvents(count);
        
        // Then
        assertNotNull(events);
        assertEquals(count, events.length);
        for (int i = 0; i < count; i++) {
            assertNotNull(events[i]);
            assertNotNull(events[i].getEventId());
            assertTrue(events[i].getPayload().contains("user" + i + "@example.com"));
        }
    }
}