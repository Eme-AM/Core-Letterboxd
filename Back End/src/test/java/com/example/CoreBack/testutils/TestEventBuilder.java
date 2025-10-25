package com.example.CoreBack.testutils;

import com.example.CoreBack.entity.EventDTO;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder pattern para crear EventDTO en tests
 * Adaptado a la estructura limpia del core (solo campos básicos del CloudEvent)
 */
public class TestEventBuilder {
    private EventDTO event;
    private Map<String, Object> dataMap;
    private boolean isInvalid = false;
    
    private TestEventBuilder() {
        this.event = new EventDTO();
        this.dataMap = new HashMap<>();
    }
    
    public static TestEventBuilder builder() {
        return new TestEventBuilder();
    }
    
    public static TestEventBuilder anEvent() {
        return new TestEventBuilder();
    }
    
    // Métodos para campos básicos del CloudEvent
    public TestEventBuilder withType(String eventType) {
        this.event.setType(eventType);
        return this;
    }
    
    public TestEventBuilder withId(String id) {
        this.event.setId(id);
        return this;
    }
    
    public TestEventBuilder withSource(String source) {
        this.event.setSource(source);
        return this;
    }
    
    public TestEventBuilder withDataContentType(String dataContentType) {
        this.event.setDatacontenttype(dataContentType);
        return this;
    }
    
    public TestEventBuilder withSysDate(LocalDateTime sysDate) {
        this.event.setSysDate(sysDate);
        return this;
    }
    
    // Métodos para datos específicos que van en el Map data
    public TestEventBuilder withDataField(String key, Object value) {
        this.dataMap.put(key, value);
        return this;
    }
    
    public TestEventBuilder withUserId(String userId) {
        this.dataMap.put("userId", userId);
        return this;
    }
    
    public TestEventBuilder withMovieId(String movieId) {
        this.dataMap.put("movieId", movieId);
        return this;
    }
    
    public TestEventBuilder withRating(Double rating) {
        this.dataMap.put("rating", rating);
        return this;
    }
    
    public TestEventBuilder withEmail(String email) {
        this.dataMap.put("email", email);
        return this;
    }
    
    public TestEventBuilder withUsername(String username) {
        this.dataMap.put("username", username);
        return this;
    }
    
    // Métodos helper para crear eventos específicos
    public TestEventBuilder asUserCreated(Long userId, String email, String username) {
        this.event.setId("user-event-" + userId);
        this.event.setType("user.created");
        this.event.setSource("/users/signup");
        this.event.setDatacontenttype("application/json");
        this.event.setSysDate(LocalDateTime.now());
        
        // Store data in the map
        this.dataMap.put("userId", userId);
        this.dataMap.put("email", email);   
        this.dataMap.put("username", username);
        
        return this;
    }
    
    public TestEventBuilder asInvalid() {
        this.event = new EventDTO();
        this.dataMap.clear();
        this.isInvalid = true;
        return this;
    }
    
    public EventDTO build() {
        // Set the data map if it has content
        if (!dataMap.isEmpty()) {
            this.event.setData(new HashMap<>(dataMap));
        }
        
        // Set defaults if not set (only for valid events)
        if (!isInvalid) {
            if (event.getId() == null) {
                event.setId("test-event-" + System.currentTimeMillis());
            }
            if (event.getType() == null) {
                event.setType("test.event");
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
