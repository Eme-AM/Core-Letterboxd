package com.uade.tpo.demo.controllers;

import com.uade.tpo.demo.models.requests.EventFilterRequest;
import com.uade.tpo.demo.models.requests.EventPublishRequest;
import com.uade.tpo.demo.models.responses.EventMessageDTO;
import com.uade.tpo.demo.models.responses.EventStatsDTO;
import com.uade.tpo.demo.service.EventHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event Hub", description = "Core messaging hub for inter-module communication")
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
public class EventHubController {

    private final EventHubService eventHubService;

    @PostMapping("/publish")
    @Operation(summary = "Publish a new event", description = "Publishes an event to the message hub for distribution to target modules")
    public ResponseEntity<Object> publishEvent(@Valid @RequestBody EventPublishRequest request) {
        try {
            EventMessageDTO publishedEvent = eventHubService.publishEvent(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event published successfully");
            response.put("data", publishedEvent);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to publish event: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Filter events", description = "Get filtered and paginated list of events")
    public ResponseEntity<Object> filterEvents(@RequestBody EventFilterRequest request) {
        try {
            Page<EventMessageDTO> events = eventHubService.getEventsByFilters(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", events.getContent());
            response.put("totalElements", events.getTotalElements());
            response.put("totalPages", events.getTotalPages());
            response.put("currentPage", events.getNumber());
            response.put("pageSize", events.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to filter events: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending events", description = "Get all events that are pending processing")
    public ResponseEntity<Object> getPendingEvents() {
        try {
            List<EventMessageDTO> pendingEvents = eventHubService.getPendingEvents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pendingEvents);
            response.put("count", pendingEvents.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get pending events: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get failed events for retry", description = "Get events that failed but can still be retried")
    public ResponseEntity<Object> getFailedEventsForRetry() {
        try {
            List<EventMessageDTO> failedEvents = eventHubService.getFailedEventsForRetry();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", failedEvents);
            response.put("count", failedEvents.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get failed events: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get event statistics", description = "Get comprehensive statistics about events in the system")
    public ResponseEntity<Object> getEventStatistics() {
        try {
            EventStatsDTO stats = eventHubService.getEventStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get event statistics: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{eventId}/processing")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark event as processing", description = "Updates event status to processing")
    public ResponseEntity<Object> markEventAsProcessing(@PathVariable Long eventId) {
        try {
            eventHubService.markEventAsProcessing(eventId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event marked as processing");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update event status: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{eventId}/delivered")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark event as delivered", description = "Updates event status to delivered")
    public ResponseEntity<Object> markEventAsDelivered(@PathVariable Long eventId) {
        try {
            eventHubService.markEventAsDelivered(eventId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event marked as delivered");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update event status: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{eventId}/failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark event as failed", description = "Updates event status to failed and increments retry count")
    public ResponseEntity<Object> markEventAsFailed(@PathVariable Long eventId, 
                                                   @RequestParam(required = false) String errorMessage) {
        try {
            eventHubService.markEventAsFailed(eventId, errorMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Event marked as failed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update event status: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
