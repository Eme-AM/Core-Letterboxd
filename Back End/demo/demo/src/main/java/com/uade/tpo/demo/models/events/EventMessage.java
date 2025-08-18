package com.uade.tpo.demo.models.events;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "event_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventMessage {
    
    @Id
    @Column(name = "event_id")
    private String eventId;
    
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    @Column(name = "source_module", nullable = false)
    private String sourceModule;
    
    @Column(name = "target_module")
    private String targetModule; // null for broadcast
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @ElementCollection
    @CollectionTable(
        name = "event_payload", 
        joinColumns = @JoinColumn(name = "event_id")
    )
    @MapKeyColumn(name = "payload_key")
    @Column(name = "payload_value", length = 2000)
    private Map<String, String> payload;
    
    @Column(name = "retry_count")
    @Builder.Default
    private int retryCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.PENDING;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
