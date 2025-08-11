package com.uade.tpo.demo.models.objects;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType; // "MOVIE_CREATED", "REVIEW_ADDED", "USER_FOLLOWED", etc.

    @Column(name = "source_module", nullable = false)
    private String sourceModule; // "MOVIES", "REVIEWS", "USERS", "SOCIAL", etc.

    @Column(name = "target_module")
    private String targetModule; // "DISCOVERY", "ANALYTICS", "SOCIAL", etc. (can be null for broadcast)

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload; // JSON data of the event

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private EventStatus status = EventStatus.PENDING;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0; // 0 = normal, higher number = higher priority

    @Column(name = "correlation_id")
    private String correlationId; // To track related events

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = EventStatus.PENDING;
        }
        if (retryCount == null) {
            retryCount = 0;
        }
        if (maxRetries == null) {
            maxRetries = 3;
        }
        if (priority == null) {
            priority = 0;
        }
    }

    public enum EventStatus {
        PENDING,
        PROCESSING,
        DELIVERED,
        FAILED,
        DEAD_LETTER
    }
}
