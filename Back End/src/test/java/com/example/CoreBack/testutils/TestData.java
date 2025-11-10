package com.example.CoreBack.testutils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class TestData {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);
    
    public static class Events {
        
        private static EventDTO createEvent(String type, String source, Map<String, Object> data) {
            EventDTO event = new EventDTO();
            event.setType(type);
            event.setSource(source);
            event.setDatacontenttype("application/json");
            event.setSysDate(NOW);
            event.setData(data);
            return event;
        }
        
        public static EventDTO validEventDTO() {
            return userCreated(12345L, "test@example.com", "testuser");
        }
        
        public static EventDTO userCreated(Long userId, String email, String username) {
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("email", email);
            data.put("username", username);
            return createEvent("user.created", "/users/signup", data);
        }
        
        public static EventDTO createUserCreatedEvent(Long userId, String email, String username) {
            return userCreated(userId, email, username);
        }
        
        public static EventDTO userEvent(String userId) {
            Map<String, Object> data = Map.of("userId", userId, "action", "user_creation");
            return createEvent("user.created", "/users/signup", data);
        }
        
        public static EventDTO movieEvent(String movieId) {
            Map<String, Object> data = Map.of("movieId", movieId, "action", "movie_creation");
            return createEvent("movie.created", "/movies/add", data);
        }
        
        public static EventDTO ratingEvent(String userId, String movieId, Double rating) {
            Map<String, Object> data = Map.of("userId", userId, "movieId", movieId, "rating", rating);
            return createEvent("rating.created", "/ratings/add", data);
        }
        
        public static EventDTO complexEvent() {
            EventDTO event = new EventDTO();
            event.setType("complex.action");
            event.setSource("/complex/action");
            event.setDatacontenttype("application/json");
            event.setSysDate(NOW);
            
            // Create complex data map matching original EventTestDataFactory
            Map<String, Object> complexData = new HashMap<>();
            complexData.put("movieId", "movie1");
            complexData.put("rating", 4.5);
            complexData.put("review", "Great movie!");
            complexData.put("userId", "user1");
            
            // Nested data
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", "user1");
            userInfo.put("name", "Test User");
            complexData.put("user", userInfo);
            
            // Add tags
            complexData.put("tags", Arrays.asList("drama", "romance", "thriller"));
            
            event.setData(complexData);
            return event;
        }
        
        public static EventDTO invalidEvent() {
            return new EventDTO(); // campos nulos a propósito
        }
        
        // ===== STORED EVENTS =====
        
        private static StoredEvent createStoredEvent(String type, String source, Map<String, Object> payload) {
            StoredEvent event = new StoredEvent(type, source, "application/json", mapToJson(payload), LocalDateTime.now());
            event.setEventId(UUID.randomUUID().toString());
            return event;
        }
        
        public static StoredEvent storedEvent(String eventId, String eventType) {
            StoredEvent event = new StoredEvent(eventType, "test_source", "application/json", "{}", LocalDateTime.now());
            event.setEventId(eventId);
            return event;
        }
        
        public static StoredEvent validStoredEvent() {
            return userCreatedStored(12345L, "test@example.com", "testuser");
        }
        
        public static StoredEvent userCreatedStored(Long userId, String email, String username) {
            Map<String, Object> payload = Map.of("userId", userId, "email", email, "username", username);
            return createStoredEvent("user.created", "/users/signup", payload);
        }
        
        public static StoredEvent movieCreatedStored(Long movieId, String title, Integer year) {
            Map<String, Object> payload = Map.of("movieId", movieId, "title", title, "year", year, "genre", "Drama");
            return createStoredEvent("movie.created", "/movies/add", payload);
        }
        
        public static StoredEvent[] multipleStoredEvents(int count) {
            StoredEvent[] events = new StoredEvent[count];
            for (int i = 0; i < count; i++) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("userId", 1000L + i);
                payload.put("email", "user" + i + "@example.com");
                payload.put("username", "user" + i);
                
                StoredEvent event = new StoredEvent(
                    "user.created",
                    "/users/signup",
                    "application/json",
                    mapToJson(payload),
                    LocalDateTime.now().minusMinutes(i)
                );
                event.setEventId(UUID.randomUUID().toString());
                events[i] = event;
            }
            return events;
        }
        
        public static StoredEventBuilder stored() {
            return new StoredEventBuilder();
        }
    }
    
    // ===== SECURITY (para tests) =====
    
    public static class Security {
        public static SecurityFilterChain permitAllFilterChain(HttpSecurity http) throws Exception {
            return http
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
        }
    }
    
    // ===== BUILDER NAMESPACE =====
    
    public static class Builder {
        public static EventBuilder event() { return new EventBuilder(); }
        public static EventBuilder anEvent() { return new EventBuilder(); }
    }
    
    // ===== UTILS =====
    
    private static String mapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize test data to JSON", e);
        }
    }
    
    // ===== INNER BUILDER CLASSES =====
    
    public static class EventBuilder {
        private EventDTO event = new EventDTO();
        private final Map<String, Object> dataMap = new HashMap<>();
        
        public EventBuilder withType(String eventType) { this.event.setType(eventType); return this; }
        public EventBuilder withId(String id) { return this; } // no-op
        public EventBuilder withSource(String source) { this.event.setSource(source); return this; }
        public EventBuilder withDataContentType(String dataContentType) { this.event.setDatacontenttype(dataContentType); return this; }
        
        public EventBuilder withSysDate(OffsetDateTime sysDate) { this.event.setSysDate(sysDate); return this; }
        public EventBuilder withSysDate(LocalDateTime ldt) { this.event.setSysDate(ldt.atOffset(ZoneOffset.UTC)); return this; }
        
        public EventBuilder withData(String key, Object value) { this.dataMap.put(key, value); return this; }
        public EventBuilder withDataField(String key, Object value) { this.dataMap.put(key, value); return this; }
        
        public EventBuilder asUserCreated(Long userId, String email, String username) {
            event.setType("user.created");
            event.setSource("/users/signup");
            event.setDatacontenttype("application/json");
            event.setSysDate(NOW);
            dataMap.put("userId", userId);
            dataMap.put("email", email);
            dataMap.put("username", username);
            return this;
        }
        
        public EventBuilder asInvalid() {
            this.event = new EventDTO();
            this.dataMap.clear();
            return this;
        }
        
        public EventDTO build() {
            if (!dataMap.isEmpty()) event.setData(new HashMap<>(dataMap));
            return event;
        }
    }
    
    public static class StoredEventBuilder {
        private String eventId = UUID.randomUUID().toString();
        private String eventType = "test.event";
        private String source = "/test/source";
        private String contentType = "application/json";
        private LocalDateTime occurredAt = LocalDateTime.now();
        private final Map<String, Object> payload = new HashMap<>();
        
        public StoredEventBuilder withId(String eventId) { this.eventId = eventId; return this; }
        public StoredEventBuilder ofType(String eventType) { this.eventType = eventType; return this; }
        public StoredEventBuilder withSource(String source) { this.source = source; return this; }
        public StoredEventBuilder withContentType(String contentType) { this.contentType = contentType; return this; }
        public StoredEventBuilder occurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; return this; }
        public StoredEventBuilder withPayload(String key, Object value) { this.payload.put(key, value); return this; }
        
        public StoredEvent build() {
            StoredEvent event = new StoredEvent(eventType, source, contentType, mapToJson(payload), occurredAt);
            event.setEventId(eventId);
            return event;
        }
    }
}
