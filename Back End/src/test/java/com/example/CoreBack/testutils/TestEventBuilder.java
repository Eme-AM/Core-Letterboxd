package com.example.CoreBack.testutils;

import com.example.CoreBack.entity.EventDTO;
import java.time.LocalDateTime;

public class TestEventBuilder {
    private EventDTO event;
    private boolean isInvalid = false;
    
    private TestEventBuilder() {
        this.event = new EventDTO();
    }
    
    public static TestEventBuilder builder() {
        return new TestEventBuilder();
    }
    
    public static TestEventBuilder anEvent() {
        return new TestEventBuilder();
    }
    
    public TestEventBuilder withEventType(String eventType) {
        this.event.setEventType(eventType);
        return this;
    }
    
    public TestEventBuilder withUserId(String userId) {
        this.event.setUserId(userId);
        return this;
    }
    
    public TestEventBuilder withMovieId(String movieId) {
        this.event.setMovieId(movieId);
        return this;
    }
    
    public TestEventBuilder withRating(Double rating) {
        this.event.setRating(rating);
        return this;
    }
    
    public TestEventBuilder withSource(String source) {
        this.event.setSource(source);
        return this;
    }
    
    public TestEventBuilder withEventData(String eventData) {
        this.event.setEventData(eventData);
        return this;
    }
    
    public TestEventBuilder withDate(LocalDateTime date) {
        this.event.setSysDate(date);
        return this;
    }
    
    public TestEventBuilder withSysDate(LocalDateTime sysDate) {
        this.event.setSysDate(sysDate);
        return this;
    }
    
    public TestEventBuilder withDataField(String key, Object value) {
        if (this.event.getData() == null) {
            this.event.setData(new java.util.HashMap<>());
        }
        this.event.getData().put(key, value);
        return this;
    }
    
    public TestEventBuilder withType(String type) {
        this.event.setType(type);
        return this;
    }
    
    public TestEventBuilder asUserEvent(String userId) {
        this.event.setEventType("user_action");
        this.event.setUserId(userId);
        this.event.setEventData("{\"action\": \"test_action\"}");
        return this;
    }
    
    public TestEventBuilder withId(String id) {
        this.event.setId(id);
        return this;
    }
    
    public TestEventBuilder asUserCreated(Long userId, String email, String username) {
        this.event.setId("user-event-" + userId);
        this.event.setEventType("user.created");
        this.event.setSource("/users/signup");
        this.event.setDatacontenttype("application/json");
        this.event.setSysDate(LocalDateTime.now());
        // Store userId, email, and username in data map to match test expectations
        if (this.event.getData() == null) {
            this.event.setData(new java.util.HashMap<>());
        }
        this.event.getData().put("userId", userId); // Store as Long
        this.event.getData().put("email", email);   // Store email
        this.event.getData().put("username", username); // Store username
        this.event.setEventData("{\"userId\":\"" + userId + "\",\"email\":\"" + email + "\",\"username\":\"" + username + "\"}");
        return this;
    }
    
    public TestEventBuilder asInvalid() {
        // Don't set required fields to make it invalid
        // Clear any defaults that might have been set
        this.event = new EventDTO(); // Reset to ensure invalidity
        this.isInvalid = true; // Flag to skip defaults in build()
        // Don't set any fields - leave them all null to trigger validation errors
        return this;
    }
    
    public EventDTO build() {
        // Set defaults if not set (only for valid events)
        if (!isInvalid) {
            if (event.getId() == null) {
                event.setId("test-event-" + System.currentTimeMillis());
            }
            if (event.getType() == null && event.getEventType() == null) {
                event.setEventType("test.event");
            }
            if (event.getSource() == null) {
                event.setSource("/test");
            }
            if (event.getDatacontenttype() == null) {
                event.setDatacontenttype("application/json");
            }
            if (event.getSysDate() == null) {
                event.setSysDate(LocalDateTime.now());
            }
        }
        
        return this.event;
    }
}
