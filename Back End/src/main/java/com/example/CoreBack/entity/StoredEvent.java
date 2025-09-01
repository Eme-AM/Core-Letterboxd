package com.example.CoreBack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class StoredEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String eventType;
    private String source;
    private String version;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime occurredAt;

    public StoredEvent() {}

    public StoredEvent(String eventId, String eventType, String source, String version, String payload, LocalDateTime occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.source = source;
        this.version = version;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    // getters y setters
    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}

