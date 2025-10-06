package com.example.CoreBack.testutils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;

public class EventTestDataFactory {
    
    public static StoredEvent createStoredEvent(String eventType, String eventData) {
        return new StoredEvent(eventType, eventData, "test_stream_name", "test_stream", "test_partition", LocalDateTime.now());
    }
    
    public static EventDTO createUserEvent(String userId) {
        EventDTO event = new EventDTO();
        event.setId("user-event-" + System.currentTimeMillis());
        event.setEventType("user_action");
        event.setSource("/users/action");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        event.setUserId(userId);
        event.setEventData("{\"action\": \"test_action\"}");
        return event;
    }
    
    public static EventDTO createMovieEvent(String movieId) {
        EventDTO event = new EventDTO();
        event.setId("movie-event-" + System.currentTimeMillis());
        event.setEventType("movie_action");
        event.setSource("/movies/action");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        event.setMovieId(movieId);
        event.setEventData("{\"movie\": \"test_movie\"}");
        return event;
    }
    
    public static EventDTO createRatingEvent() {
        EventDTO event = new EventDTO();
        event.setId("rating-event-" + System.currentTimeMillis());
        event.setEventType("rating");
        event.setSource("/ratings/create");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        event.setUserId("user1");
        event.setMovieId("movie1");
        event.setRating(5.0);
        event.setEventData("{\"rating\": 5.0}");
        return event;
    }
    
    public static EventDTO createEventDTOWithComplexData() {
        EventDTO event = new EventDTO();
        event.setId("complex-event-" + System.currentTimeMillis());
        event.setEventType("complex_action");
        event.setSource("/complex/action");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        event.setUserId("user1");
        event.setMovieId("movie1");
        event.setRating(4.5);
        event.setReview("Great movie!");
        
        // Create complex data map
        Map<String, Object> complexData = new HashMap<>();
        complexData.put("movieId", "movie1");
        complexData.put("rating", 4.5);
        complexData.put("review", "Great movie!");
        complexData.put("tags", List.of("action", "sci-fi"));
        complexData.put("user", Map.of("id", "user1", "name", "Test User"));
        
        event.setData(complexData);
        event.setEventData("{\"movieId\":\"movie1\",\"rating\":4.5,\"review\":\"Great movie!\"}");
        return event;
    }
    
    public static EventDTO createValidEventDTO() {
        EventDTO event = new EventDTO();
        event.setId("test-event-123");
        event.setEventType("user.created");
        event.setSource("/users/signup");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        event.setUserId("12345");
        event.setEventData("{\"userId\":\"12345\",\"email\":\"user@example.com\"}");
        return event;
    }
    
    public static EventDTO createUserCreatedEvent(Long userId, String email, String username) {
        EventDTO event = new EventDTO();
        event.setId("user-event-" + userId);
        event.setEventType("user.created");
        event.setSource("/users/signup");
        event.setDatacontenttype("application/json");
        event.setSysDate(LocalDateTime.now());
        // Use Long directly to match test expectations and store all data
        if (event.getData() == null) {
            event.setData(new java.util.HashMap<>());
        }
        event.getData().put("userId", userId); // Store as Long, not String
        event.getData().put("email", email);   // Store email
        event.getData().put("username", username); // Store username
        event.setEventData("{\"userId\":\"" + userId + "\",\"username\":\"" + username + "\",\"email\":\"" + email + "\"}");
        return event;
    }
    
    public static EventDTO createInvalidEventDTO() {
        EventDTO event = new EventDTO();
        // Missing required fields intentionally to trigger validation errors
        // Only set some fields, leave others null
        // Missing: id, type, source, datacontenttype, sysDate
        return event;
    }
}
