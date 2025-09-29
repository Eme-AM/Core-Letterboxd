package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public EventController(EventRepository eventRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    // ============================================================
    // 1. Listar eventos con filtros
    // ============================================================
    @Operation(summary = "Obtener todos los eventos", description = "Devuelve una lista paginada de eventos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error en la consulta de eventos")
    })
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(eventService.getAllEvents(page, size, module, status, search));
    }

    // ============================================================
    // 2. Detalle de un evento
    // ============================================================
    @Operation(summary = "Obtener detalle de un evento", description = "Devuelve toda la información de un evento")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventDetail(@PathVariable Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> ResponseEntity.ok(Map.of(
                        "eventId", event.getEventId(),
                        "type", event.getEventType(),
                        "source", event.getSource(),
                        "status", event.getStatus(),
                        "timeline", List.of(
                                Map.of("step", "Received", "time", event.getOccurredAt()),
                                Map.of("step", "Stored", "time", event.getOccurredAt().plusSeconds(2)),
                                Map.of("step", "Delivered", "time", event.getOccurredAt().plusSeconds(5))
                        ),
                        "payload", event.getPayload()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================================================
    // 3. Recibir un nuevo evento
    // ============================================================
    @Operation(summary = "Recibir un nuevo evento", description = "Procesa un evento entrante y lo almacena")
    @Parameter(name = "routingKey", description = "Clave de enrutamiento", example = "core.routing")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento recibido correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento")
    })
    @PostMapping("/receive")
    public ResponseEntity<?> receiveEvent(
            @Valid @RequestBody EventDTO eventDTO,
            @RequestParam(defaultValue = "core.routing") String routingKey
    ) {
        try {
            StoredEvent storedEvent = eventService.processIncomingEvent(eventDTO, routingKey);
            return ResponseEntity.ok(Map.of(
                    "status", "received",
                    "eventId", storedEvent.getEventId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    // ============================================================
    // 4. Estadísticas globales
    // ============================================================
    @Operation(summary = "Obtener estadísticas globales", description = "Métricas de eventos (totales, fallidos, entregados, en cola)")
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(eventService.getGlobalStats());
    }

    // ============================================================
    // 5. Evolución últimas 24h
    // ============================================================
    @Operation(summary = "Evolución de eventos", description = "Cantidad de eventos por hora en las últimas 24h")
    @GetMapping("/evolution")
    public ResponseEntity<?> getEvolution() {
        return ResponseEntity.ok(eventService.getEvolution());
    }

    // ============================================================
    // 6. Eventos por módulo
    // ============================================================
    @Operation(summary = "Eventos por módulo", description = "Devuelve un conteo agrupado por módulo")
    @GetMapping("/per-module")
    public ResponseEntity<?> getEventsPerModule() {
        return ResponseEntity.ok(eventService.getEventsPerModule());
    }
}