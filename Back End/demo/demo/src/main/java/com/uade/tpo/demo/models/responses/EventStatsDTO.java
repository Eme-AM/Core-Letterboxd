package com.uade.tpo.demo.models.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventStatsDTO {

    private Long totalEvents;
    private Long pendingEvents;
    private Long processingEvents;
    private Long deliveredEvents;
    private Long failedEvents;
    private Long deadLetterEvents;
    
    // Stats by module
    private Long moviesModuleEvents;
    private Long usersModuleEvents;
    private Long reviewsModuleEvents;
    private Long socialModuleEvents;
    private Long discoveryModuleEvents;
    private Long analyticsModuleEvents;
    
    // Recent activity
    private Long eventsLast24Hours;
    private Long eventsLastHour;
    private Double averageProcessingTime; // in seconds
}
