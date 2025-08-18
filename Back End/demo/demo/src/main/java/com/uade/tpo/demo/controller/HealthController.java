package com.uade.tpo.demo.controller;

import com.uade.tpo.demo.models.dto.HealthStatus;
import com.uade.tpo.demo.service.HealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/core/health")
@RequiredArgsConstructor
@Tag(name = "Health Management", description = "APIs for monitoring system health and status")
public class HealthController {
    
    private final HealthService healthService;
    
    @GetMapping
    @Operation(summary = "Get system health status", description = "Retrieve comprehensive health status of all system components")
    public ResponseEntity<HealthStatus> getHealthStatus() {
        HealthStatus health = healthService.getHealthStatus();
        return ResponseEntity.ok(health);
    }
}
