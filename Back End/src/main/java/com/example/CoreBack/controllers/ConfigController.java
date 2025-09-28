package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.RetryPolicyDTO;
import com.example.CoreBack.service.ConfigService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/config")
@Tag(name = "Configuración", description = "Gestión de policies y colas/tópicos")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    // ================================
    // Retry Policies
    // ================================
    @Operation(summary = "Crear retry policy")
    @PostMapping("/policies/retry")
    public ResponseEntity<?> createRetryPolicy(@RequestBody RetryPolicyDTO dto) {
        // TODO: implementar creación de retry policy
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Listar retry policies")
    @GetMapping("/policies/retry")
    public ResponseEntity<?> listRetryPolicies() {
        // TODO: implementar listado de retry policies
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Obtener retry policy")
    @GetMapping("/policies/retry/{policyId}")
    public ResponseEntity<?> getRetryPolicy(@PathVariable String policyId) {
        // TODO: implementar obtención de retry policy
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Actualizar retry policy")
    @PutMapping("/policies/retry/{policyId}")
    public ResponseEntity<?> updateRetryPolicy(@PathVariable String policyId,
                                               @RequestBody RetryPolicyDTO dto) {
        // TODO: implementar actualización de retry policy
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Eliminar retry policy")
    @DeleteMapping("/policies/retry/{policyId}")
    public ResponseEntity<?> deleteRetryPolicy(@PathVariable String policyId) {
        // TODO: implementar eliminación de retry policy
        return ResponseEntity.status(501).body("Not Implemented");
    }

}
