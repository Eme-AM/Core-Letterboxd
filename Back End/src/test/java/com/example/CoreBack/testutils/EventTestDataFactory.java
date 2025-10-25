package com.example.CoreBack.testutils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;

/**
 * Factory para crear objetos EventDTO y StoredEvent para tests
 * Adaptado a la estructura limpia del core (solo campos básicos del CloudEvent)
 */
public class EventTestDataFactory {
    
    public static StoredEvent createStoredEvent(String eventType, String eventData) {
        return new StoredEvent(eventType, eventData, "test_stream_name", "test_stream", "test_partition", LocalDateTime.now());
    }
    
    /**
     * Crea un evento de usuario con estructura básica
     */
    public static EventDTO createUserEvent(String userId) {
        EventDTO event = new EventDTO();
        event.setId("user-event-" + System.currentTimeMillis());
        event.setType("user.created");
        event.setSource("/users/signup");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        // Todos los datos específicos van en el Map data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("action", "user_creation");
        event.setData(eventData);
        
        return event;
    }
    
    /**
     * Crea un evento de película
     */
    public static EventDTO createMovieEvent(String movieId) {
        EventDTO event = new EventDTO();
        event.setId("movie-event-" + System.currentTimeMillis());
        event.setType("movie.created");
        event.setSource("/movies/create");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("movieId", movieId);
        eventData.put("action", "movie_creation");
        event.setData(eventData);
        
        return event;
    }
    
    /**
     * Crea un evento de rating
     */
    public static EventDTO createRatingEvent(String userId, String movieId, Double rating) {
        EventDTO event = new EventDTO();
        event.setId("rating-event-" + System.currentTimeMillis());
        event.setType("rating.created");
        event.setSource("/ratings/create");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("movieId", movieId);
        eventData.put("rating", rating);
        event.setData(eventData);
        
        return event;
    }
    
    /**
     * Crea un evento con data compleja para tests
     */
    public static EventDTO createEventDTOWithComplexData() {
        EventDTO event = new EventDTO();
        event.setId("complex-event-" + System.currentTimeMillis());
        event.setType("complex.action");
        event.setSource("/complex/action");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        // Create complex data map
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
    
    /**
     * Crea un evento válido básico
     */
    public static EventDTO createValidEventDTO() {
        EventDTO event = new EventDTO();
        event.setId("test-event-123");
        event.setType("user.created");
        event.setSource("/users/signup");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", "12345");
        eventData.put("email", "user@example.com");
        event.setData(eventData);
        
        return event;
    }
    
    /**
     * Crea un evento de usuario creado con parámetros específicos
     */
    public static EventDTO createUserCreatedEvent(Long userId, String email, String username) {
        EventDTO event = new EventDTO();
        event.setId("user-created-" + userId);
        event.setType("user.created");
        event.setSource("/users/signup");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("userId", userId);
        eventData.put("username", username);
        eventData.put("email", email);
        event.setData(eventData);
        
        return event;
    }
    
    /**
     * Crea un evento inválido para tests de validación
     */
    public static EventDTO createInvalidEventDTO() {
        EventDTO event = new EventDTO();
        // Missing required fields intentionally to trigger validation errors
        // Only set some fields, leave others null
        // Missing: id, type, source, datacontenttype, data
        return event;
    }
}
