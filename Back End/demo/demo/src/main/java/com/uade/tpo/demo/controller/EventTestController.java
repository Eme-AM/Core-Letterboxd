package com.uade.tpo.demo.controller;

import com.uade.tpo.demo.models.events.*;
import com.uade.tpo.demo.service.EventHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing event publishing and RabbitMQ integration
 */
@RestController
@RequestMapping("/events/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Event Testing", description = "APIs for testing RabbitMQ event publishing")
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
public class EventTestController {

    private final EventHubService eventHubService;

    @PostMapping("/movie")
    @Operation(summary = "Test movie event publishing")
    public ResponseEntity<Map<String, Object>> testMovieEvent(
            @RequestParam String action,
            @RequestParam String movieId,
            @RequestParam(required = false) String title) {
        
        try {
            Map<String, Object> movieData = new HashMap<>();
            movieData.put("title", title != null ? title : "Test Movie");
            movieData.put("genre", "Action");
            movieData.put("year", 2024);
            
            MovieEvent event = new MovieEvent(action, movieId, movieData);
            event.setUserId("test-user");
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "Movie event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish movie event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/user")
    @Operation(summary = "Test user event publishing")
    public ResponseEntity<Map<String, Object>> testUserEvent(
            @RequestParam String action,
            @RequestParam String userId,
            @RequestParam(required = false) String email) {
        
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email != null ? email : "test@example.com");
            userData.put("username", "testuser");
            userData.put("country", "Argentina");
            
            UserEvent event = new UserEvent(action, userId, userData);
            event.setUserId("system");
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "User event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish user event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/review")
    @Operation(summary = "Test review event publishing")
    public ResponseEntity<Map<String, Object>> testReviewEvent(
            @RequestParam String action,
            @RequestParam String reviewId,
            @RequestParam String movieId,
            @RequestParam(required = false) Double rating) {
        
        try {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("content", "This is a test review");
            reviewData.put("rating", rating != null ? rating : 4.5);
            reviewData.put("isPublic", true);
            
            ReviewEvent event = new ReviewEvent(action, reviewId, movieId, reviewData);
            event.setUserId("test-reviewer");
            event.setRating(rating != null ? rating : 4.5);
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "Review event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish review event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/social")
    @Operation(summary = "Test social event publishing")
    public ResponseEntity<Map<String, Object>> testSocialEvent(
            @RequestParam String action,
            @RequestParam String targetUserId,
            @RequestParam String contentId,
            @RequestParam String contentType) {
        
        try {
            Map<String, Object> socialData = new HashMap<>();
            socialData.put("timestamp", System.currentTimeMillis());
            socialData.put("source", "web");
            
            SocialEvent event = new SocialEvent(action, targetUserId, contentId, contentType, socialData);
            event.setUserId("social-user");
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "Social event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish social event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/discovery")
    @Operation(summary = "Test discovery event publishing")
    public ResponseEntity<Map<String, Object>> testDiscoveryEvent(
            @RequestParam String action,
            @RequestParam(required = false) String searchQuery) {
        
        try {
            Map<String, Object> discoveryData = new HashMap<>();
            discoveryData.put("searchQuery", searchQuery != null ? searchQuery : "test query");
            discoveryData.put("filters", Map.of("genre", "action", "year", 2024));
            discoveryData.put("resultCount", 42);
            
            DiscoveryEvent event = new DiscoveryEvent(action, discoveryData);
            event.setUserId("discovery-user");
            event.setSearchQuery(searchQuery);
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "Discovery event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish discovery event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/analytics")
    @Operation(summary = "Test analytics event publishing")
    public ResponseEntity<Map<String, Object>> testAnalyticsEvent(
            @RequestParam String action,
            @RequestParam String metricType,
            @RequestParam Object metricValue) {
        
        try {
            Map<String, Object> analyticsData = new HashMap<>();
            analyticsData.put("metric", metricType);
            analyticsData.put("value", metricValue);
            analyticsData.put("timestamp", System.currentTimeMillis());
            
            AnalyticsEvent event = new AnalyticsEvent(action, metricType, metricValue, analyticsData);
            event.setUserId("analytics-system");
            
            eventHubService.publishEvent(event);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getEventId());
            response.put("message", "Analytics event published successfully");
            response.put("routingKey", event.getRoutingKey());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to publish analytics event: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Check RabbitMQ connection health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("message", "Event testing endpoints are ready");
        response.put("availableEndpoints", Map.of(
            "movie", "/api/events/test/movie",
            "user", "/api/events/test/user",
            "review", "/api/events/test/review",
            "social", "/api/events/test/social",
            "discovery", "/api/events/test/discovery",
            "analytics", "/api/events/test/analytics"
        ));
        
        return ResponseEntity.ok(response);
    }
}
