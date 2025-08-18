package com.uade.tpo.demo.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class EventDto {
    
    @NotBlank(message = "Event type is required")
    private String eventType;
    
    @NotBlank(message = "Source module is required")
    private String sourceModule;
    
    private String targetModule; // optional, null for broadcast
    
    @NotNull(message = "Payload is required")
    private Map<String, Object> payload;
    
    private LocalDateTime timestamp;
}
