package com.example.CoreBack.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class StoredEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;
    private String eventType;
    private String source;
    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime occurredAt;

    @Column(nullable = false)
    private String status = "InQueue"; // InQueue, Delivered, Failed

    public StoredEvent() {}

    public StoredEvent(String eventId, String eventType, String source,
                       String contentType, String payload, LocalDateTime occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.source = source;
        this.contentType = contentType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.status = "InQueue";
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}