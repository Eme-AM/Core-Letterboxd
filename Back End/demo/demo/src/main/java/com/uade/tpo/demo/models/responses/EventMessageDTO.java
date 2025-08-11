package com.uade.tpo.demo.models.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventMessageDTO {

    private Long id;
    private String eventType;
    private String sourceModule;
    private String targetModule;
    private String payload;
    private String status;
    private Integer retryCount;
    private Integer maxRetries;
    private Integer priority;
    private String correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String errorMessage;
}
