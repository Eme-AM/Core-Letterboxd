package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.RetryPolicyDTO;
import com.example.CoreBack.service.ConfigService;
import com.example.CoreBack.entity.SystemConfigDTO;
import com.example.CoreBack.entity.ModulePolicyDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/config")
@Tag(name = "Configuración", description = "Gestión de policies, configuración del sistema y asignación por módulos")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<RetryPolicyDTO> createRetryPolicy(@RequestBody RetryPolicyDTO dto) {
        return ResponseEntity.ok(configService.createRetryPolicy(dto));
    }

    @Operation(summary = "Listar retry policies")
    @GetMapping("/policies/retry")
    public ResponseEntity<List<RetryPolicyDTO>> listRetryPolicies() {
        return ResponseEntity.ok(configService.listRetryPolicies());
    }

    @Operation(summary = "Obtener retry policy por ID")
    @GetMapping("/policies/retry/{policyId}")
    public ResponseEntity<?> getRetryPolicy(@PathVariable Long policyId) {
        RetryPolicyDTO dto = configService.getRetryPolicy(policyId);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Actualizar retry policy")
    @PutMapping("/policies/retry/{policyId}")
    public ResponseEntity<RetryPolicyDTO> updateRetryPolicy(
            @PathVariable Long policyId, @RequestBody RetryPolicyDTO dto) {
        return ResponseEntity.ok(configService.updateRetryPolicy(policyId, dto));
    }

    @Operation(summary = "Eliminar retry policy")
    @DeleteMapping("/policies/retry/{policyId}")
    public ResponseEntity<Void> deleteRetryPolicy(@PathVariable Long policyId) {
        configService.deleteRetryPolicy(policyId);
        return ResponseEntity.noContent().build();
    }

    // ================================
    // System Configuration
    // ================================
    @Operation(summary = "Guardar configuración general del sistema")
    @PostMapping("/system")
    public ResponseEntity<?> saveSystemConfig(@RequestBody SystemConfigDTO dto) {
        configService.saveSystemConfig(dto);
        return ResponseEntity.ok("Configuración del sistema guardada correctamente");
    }

    @Operation(summary = "Obtener configuración general del sistema")
    @GetMapping("/system")
    public ResponseEntity<SystemConfigDTO> getSystemConfig() {
        return ResponseEntity.ok(configService.getSystemConfig());
    }

    // ================================
    // Module Policies
    // ================================
    @Operation(summary = "Asignar política a módulo")
    @PostMapping("/modules/assign-policy")
    public ResponseEntity<?> assignPolicyToModule(@RequestBody ModulePolicyDTO dto) {
        configService.assignPolicyToModule(dto);
        return ResponseEntity.ok("Política asignada correctamente al módulo");
    }

    @Operation(summary = "Listar módulos y políticas asignadas")
    @GetMapping("/modules")
    public ResponseEntity<?> getModulesAndPolicies() {
        return ResponseEntity.ok(configService.getModulesAndPolicies());
    }
}