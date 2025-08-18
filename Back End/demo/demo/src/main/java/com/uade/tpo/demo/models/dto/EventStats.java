package com.uade.tpo.demo.models.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class EventStats {
    
    private long totalEvents;
    private long pendingEvents;
    private long processedEvents;
    private long failedEvents;
    private long retryingEvents;
    private double successRate;
    private double averageProcessingTime; // in milliseconds
    
    // Events by module
    private long userModuleEvents;
    private long movieModuleEvents;
    private long reviewModuleEvents;
    private long socialModuleEvents;
    private long discoveryModuleEvents;
    private long analyticsModuleEvents;
}
