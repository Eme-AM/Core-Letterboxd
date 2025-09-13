package com.example.CoreBack.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class EventMessage {
    private String eventId;
    private String eventType;
    private String source;
    private String version;
    private LocalDateTime occurredAt;
    private Map<String, Object> payload;

    public EventMessage() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.version = "1.0";
    }

    // getters y setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }
}

