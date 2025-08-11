package com.uade.tpo.demo.models.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilterRequest {

    private String eventType;
    private String sourceModule;
    private String targetModule;
    private String status;
    private String correlationId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 20;
    @Builder.Default
    private String sortBy = "createdAt";
    @Builder.Default
    private String sortDir = "desc";
}
