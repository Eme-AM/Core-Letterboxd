package com.example.CoreBack.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(
    name = "events",
    indexes = {
        @Index(name = "idx_events_status_nextAttempt", columnList = "status,nextAttemptAt"),
        @Index(name = "idx_events_routingKey", columnList = "routingKey"),
        @Index(name = "idx_events_messageId", columnList = "messageId", unique = true)
    }
)
public class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- originales tuyos ---
    private String eventId;
    private String eventType;
    private String source;
    private String contentType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime occurredAt;

    // --- NUEVOS: soporte outbox ---
    @Column(nullable = false)
    private String routingKey;           // para saber a dónde reenviar

    @Column(nullable = false)
    private String status = "PENDING";   // PENDING | DELIVERED | FAILED

    @Column(nullable = false)
    private Integer attempts = 0;        // cantidad de reintentos hechos

    private LocalDateTime nextAttemptAt; // cuándo volver a intentar

    private LocalDateTime deliveredAt;   // cuándo se logró publicar

    @Column(length = 512)
    private String error;                // último error (debug/observabilidad)

    @Column(length = 100)
    private String messageId;            // idempotencia en consumidores

    // --- (opcional) si publicás a más de un exchange ---
    private String exchangeName;         // si no, podés ignorarlo

    // --- (opcional) control de lock si tenés múltiples workers ---
    private LocalDateTime lockedUntil;   // para evitar doble envío concurrente

    // ==== CTORs ====
    public StoredEvent() {}

    public StoredEvent(String eventType, String source,
                       String contentType, String payload, LocalDateTime occurredAt) {
        this.eventType = eventType;
        this.source = source;
        this.contentType = contentType;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.status = "PENDING";
        this.attempts = 0;
        this.nextAttemptAt = LocalDateTime.now();
    }

    // ==== getters/setters ====
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

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public LocalDateTime getNextAttemptAt() { return nextAttemptAt; }
    public void setNextAttemptAt(LocalDateTime nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
}
