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
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public EventController(EventRepository eventRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    // ============================================================
    // 1. Listar todos los eventos
    // ============================================================
    @Operation(
        summary = "Obtener todos los eventos",
        description = "Devuelve una lista con todos los eventos almacenados",
        tags = { "Eventos" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida correctamente."),
        @ApiResponse(responseCode = "500", description = "Error en la consulta de eventos.")
    })
    @GetMapping
    public List<StoredEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    // ============================================================
    // 2. Recibir un nuevo evento
    // ============================================================
    @Operation(
        summary = "Recibir un nuevo evento",
        description = "Procesa un evento entrante y lo almacena en la base de datos",
        tags = { "Eventos" }
    )
    @Parameter(
        name = "routingKey",
        description = "Clave de enrutamiento usada para procesar el evento",
        example = "core.routing",
        required = false
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento recibido y procesado correctamente."),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento del evento.")
    })
    @PostMapping("/receive")
    public ResponseEntity<?> receiveEvent(
            @Valid @RequestBody EventDTO eventDTO,
            @RequestParam(defaultValue = "core.routing") String routingKey) {
        // Lógica ya implementada
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
    // 3. Obtener un evento específico (placeholder)
    // ============================================================
    @Operation(
        summary = "Obtener un evento específico",
        description = "Devuelve la información de un evento a partir de su ID",
        tags = { "Eventos" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento encontrado."),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado.")
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable String eventId) {
        // TODO: Implementar consulta por ID
        return ResponseEntity.notFound().build();
    }

    // ============================================================
    // 4. Reintentar un evento fallido (placeholder)
    // ============================================================
    @Operation(
        summary = "Reintentar evento fallido",
        description = "Vuelve a enviar un evento que no pudo ser procesado",
        tags = { "Eventos" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento reenviado correctamente."),
        @ApiResponse(responseCode = "400", description = "No se pudo reintentar el evento.")
    })
    @PostMapping("/{eventId}/retry")
    public ResponseEntity<?> retryEvent(@PathVariable String eventId) {
        // TODO: Implementar lógica de reintento
        return ResponseEntity.badRequest().body(Map.of("status", "not_implemented"));
    }

    // ============================================================
    // 5. Obtener el timeline de un evento (placeholder)
    // ============================================================
    @Operation(
        summary = "Obtener timeline de un evento",
        description = "Devuelve la evolución o historial del evento en el sistema",
        tags = { "Eventos" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Timeline obtenido correctamente."),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado.")
    })
    @GetMapping("/{eventId}/timeline")
    public ResponseEntity<?> getEventTimeline(@PathVariable String eventId) {
        // TODO: Implementar obtención del timeline
        return ResponseEntity.badRequest().body(Map.of("status", "not_implemented"));
    }
}
