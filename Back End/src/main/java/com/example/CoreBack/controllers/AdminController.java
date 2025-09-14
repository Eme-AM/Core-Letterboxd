package com.example.CoreBack.controllers;

import com.example.CoreBack.service.AdminService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administración", description = "Consultas y estadísticas")
public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Buscar eventos con filtros avanzados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Eventos filtrados correctamente."),
        @ApiResponse(responseCode = "400", description = "Filtros inválidos.")
    })
    @GetMapping("/search/events")
    public ResponseEntity<?> searchEvents(@RequestParam Map<String, String> filters) {
        // TODO: implementar búsqueda con filtros
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Métricas generales")
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        // TODO: implementar métricas generales
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Eventos agrupados por módulo")
    @GetMapping("/stats/events-per-module")
    public ResponseEntity<?> eventsPerModule() {
        // TODO: implementar agrupación de eventos por módulo
        return ResponseEntity.status(501).body("Not Implemented");
    }

    @Operation(summary = "Evolución de eventos en el tiempo")
    @GetMapping("/stats/events-evolution")
    public ResponseEntity<?> eventsEvolution() {
        // TODO: implementar evolución de eventos
        return ResponseEntity.status(501).body("Not Implemented");
    }
}
