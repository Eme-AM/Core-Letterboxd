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
}
