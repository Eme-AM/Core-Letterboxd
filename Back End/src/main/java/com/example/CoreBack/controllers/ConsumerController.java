package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.ConsumerDTO;
import com.example.CoreBack.service.ConsumerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/consumers")
@Tag(name = "Consumidores", description = "Gestión de consumidores de eventos")
public class ConsumerController {

    private final ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    // ============================================================
    // 1. Registrar consumidor (placeholder)
    // ============================================================
    @Operation(summary = "Registrar consumidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumidor registrado correctamente."),
        @ApiResponse(responseCode = "400", description = "Error al registrar consumidor.")
    })
    @PostMapping
    public ResponseEntity<?> registerConsumer(@RequestBody ConsumerDTO consumerDTO) {
        // TODO: Implementar registro de consumidor
        return ResponseEntity.status(501).body("Not Implemented");
    }

    // ============================================================
    // 2. Listar consumidores (placeholder)
    // ============================================================
    @Operation(summary = "Listar consumidores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de consumidores obtenida correctamente.")
    })
    @GetMapping
    public ResponseEntity<List<ConsumerDTO>> getConsumers() {
        // TODO: Implementar listado de consumidores
        return ResponseEntity.status(501).build();
    }

    // ============================================================
    // 3. Obtener consumidor específico (placeholder)
    // ============================================================
    @Operation(summary = "Obtener consumidor específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumidor encontrado."),
        @ApiResponse(responseCode = "404", description = "Consumidor no encontrado.")
    })
    @GetMapping("/{consumerId}")
    public ResponseEntity<ConsumerDTO> getConsumer(@PathVariable String consumerId) {
        // TODO: Implementar obtención de consumidor
        return ResponseEntity.status(501).build();
    }

    // ============================================================
    // 4. Actualizar consumidor (placeholder)
    // ============================================================
    @Operation(summary = "Actualizar consumidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consumidor actualizado correctamente."),
        @ApiResponse(responseCode = "404", description = "Consumidor no encontrado.")
    })
    @PutMapping("/{consumerId}")
    public ResponseEntity<?> updateConsumer(@PathVariable String consumerId,
                                            @RequestBody ConsumerDTO consumerDTO) {
        // TODO: Implementar actualización de consumidor
        return ResponseEntity.status(501).body("Not Implemented");
    }

    // ============================================================
    // 5. Eliminar consumidor (placeholder)
    // ============================================================
    @Operation(summary = "Eliminar consumidor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Consumidor eliminado correctamente."),
        @ApiResponse(responseCode = "404", description = "Consumidor no encontrado.")
    })
    @DeleteMapping("/{consumerId}")
    public ResponseEntity<?> deleteConsumer(@PathVariable String consumerId) {
        // TODO: Implementar eliminación de consumidor
        return ResponseEntity.status(501).body("Not Implemented");
    }
}
