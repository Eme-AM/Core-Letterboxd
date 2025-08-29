package com.uade.tpo.demo.models.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for all events in the Letterboxd system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MovieEvent.class, name = "MOVIE_EVENT"),
    @JsonSubTypes.Type(value = UserEvent.class, name = "USER_EVENT"),
    @JsonSubTypes.Type(value = ReviewEvent.class, name = "REVIEW_EVENT"),
    @JsonSubTypes.Type(value = SocialEvent.class, name = "SOCIAL_EVENT"),
    @JsonSubTypes.Type(value = DiscoveryEvent.class, name = "DISCOVERY_EVENT"),
    @JsonSubTypes.Type(value = AnalyticsEvent.class, name = "ANALYTICS_EVENT")
})
public abstract class BaseEvent {
    
    private String eventId;
    private String eventType;
    private String source;
    private String version;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String correlationId;
    private String userId;
    private Map<String, Object> metadata;
    
    public BaseEvent(String eventType, String source) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.source = source;
        this.version = "1.0";
        this.timestamp = LocalDateTime.now();
        this.correlationId = UUID.randomUUID().toString();
    }
    
    public abstract String getRoutingKey();
}
