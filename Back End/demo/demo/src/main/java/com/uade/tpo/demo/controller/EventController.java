package com.uade.tpo.demo.controller;

import com.uade.tpo.demo.models.dto.EventDto;
import com.uade.tpo.demo.models.dto.EventStats;
import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/core/events")
@RequiredArgsConstructor
@Tag(name = "Event Management", description = "APIs for managing and monitoring events in the Core Hub")
public class EventController {
    
    private final EventService eventService;
    
    @PostMapping
    @Operation(summary = "Publish a new event", description = "Publishes a new event to the event hub")
    public ResponseEntity<EventMessage> publishEvent(@Valid @RequestBody EventDto eventDto) {
        EventMessage event = eventService.publishEvent(eventDto);
        return ResponseEntity.accepted().body(event);
    }
    
    @GetMapping
    @Operation(summary = "Search events", description = "Search and filter events with pagination")
    public ResponseEntity<Page<EventMessage>> searchEvents(
            @Parameter(description = "Filter by event type")
            @RequestParam(required = false) String eventType,
            
            @Parameter(description = "Filter by source module")
            @RequestParam(required = false) String sourceModule,
            
            @Parameter(description = "Filter by event status")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Filter events from this date and time")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            
            @Parameter(description = "Filter events until this date and time")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable
    ) {
        Page<EventMessage> events = eventService.searchEvents(eventType, sourceModule, status, from, to, pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/{eventId}")
    @Operation(summary = "Get event details", description = "Retrieve detailed information about a specific event")
    public ResponseEntity<EventMessage> getEventDetails(
            @Parameter(description = "The ID of the event to retrieve")
            @PathVariable Long eventId
    ) {
        EventMessage event = eventService.findById(eventId);
        return ResponseEntity.ok(event);
    }
    
    @PostMapping("/{eventId}/retry")
    @Operation(summary = "Retry failed event", description = "Manually retry a failed event")
    public ResponseEntity<String> retryEvent(
            @Parameter(description = "The ID of the event to retry")
            @PathVariable Long eventId
    ) {
        eventService.manualRetry(eventId);
        return ResponseEntity.ok("Event queued for retry");
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get event statistics", description = "Retrieve comprehensive statistics about event processing")
    public ResponseEntity<EventStats> getEventStats() {
        EventStats stats = eventService.getEventStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/types")
    @Operation(summary = "Get event types", description = "Get list of available event types")
    public ResponseEntity<String[]> getEventTypes() {
        String[] eventTypes = {
            "USER_REGISTERED", "USER_UPDATED", "USER_DELETED",
            "MOVIE_CREATED", "MOVIE_UPDATED", "MOVIE_DELETED",
            "REVIEW_POSTED", "REVIEW_UPDATED", "REVIEW_DELETED",
            "RATING_GIVEN", "RATING_UPDATED",
            "USER_FOLLOWED", "USER_UNFOLLOWED",
            "LIST_CREATED", "LIST_UPDATED", "LIST_SHARED",
            "RECOMMENDATION_GENERATED", "SEARCH_PERFORMED",
            "ANALYTICS_UPDATED"
        };
        return ResponseEntity.ok(eventTypes);
    }
    
    @GetMapping("/modules")
    @Operation(summary = "Get source modules", description = "Get list of source modules")
    public ResponseEntity<String[]> getSourceModules() {
        String[] modules = {
            "usuarios", "peliculas", "reviews", "social", "discovery", "analytics"
        };
        return ResponseEntity.ok(modules);
    }
}
