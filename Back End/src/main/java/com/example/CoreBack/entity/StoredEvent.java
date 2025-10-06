package com.example.CoreBack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class StoredEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID interno generado en el Core (UUID.randomUUID)
    private String eventId;

    // "user.created", "movie.created", etc.
    private String eventType;

    // De dónde viene el evento: "/users/signup", "/movies/add"...
    private String source;

    // application/json (por ahora fijo, pero guardamos por si cambia a futuro)
    private String contentType;

    // Payload completo del "data" en formato JSON
    @Column(columnDefinition = "TEXT")
    private String payload;

    // Fecha y hora en que ocurrió el evento (SysDate del JSON)
    private LocalDateTime occurredAt;

    // Nuevo campo: estado del evento
    @Column(nullable = false)
    private String status = "IN_QUEUE"; // IN_QUEUE, DELIVERED, FAILED

    public StoredEvent() {}

    public StoredEvent(String eventId, String eventType, String source,
                       String contentType, String payload, LocalDateTime occurredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.source = source;
        this.contentType = contentType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.status = "IN_QUEUE";
    }

    // Getters & setters
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