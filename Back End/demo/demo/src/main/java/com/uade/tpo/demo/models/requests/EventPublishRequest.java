package com.uade.tpo.demo.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventPublishRequest {

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotBlank(message = "Source module is required")
    private String sourceModule;

    private String targetModule; // Optional - null means broadcast to all

    @NotBlank(message = "Payload is required")
    private String payload;

    @Builder.Default
    private Integer priority = 0;

    private String correlationId;
}
