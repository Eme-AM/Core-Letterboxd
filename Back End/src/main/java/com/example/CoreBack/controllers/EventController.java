package com.example.CoreBack.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.EventEnvelope;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventBusinessValidator;
import com.example.CoreBack.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;
    private final RabbitTemplate rabbitTemplate;
    private final EventBusinessValidator biz;
    private final ObjectMapper mapper;

    public EventController(
            EventRepository eventRepository,
            EventService eventService,
            RabbitTemplate rabbitTemplate,
            EventBusinessValidator biz,
            ObjectMapper mapper
    ) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.rabbitTemplate = rabbitTemplate;
        this.biz = biz;
        this.mapper = mapper;
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
    // 3. Recibir un nuevo evento (v1 endurecido con validaciones)
    // ============================================================
    @Operation(summary = "Recibir un nuevo evento", description = "Procesa un evento entrante y lo almacena")
    @Parameter(name = "routingKey", description = "Clave de enrutamiento", example = "movie.created")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento recibido correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en el procesamiento")
    })
    @PostMapping("/receive")
    public ResponseEntity<?> receiveEvent(
            @Valid @RequestBody EventDTO eventDTO,
            @RequestParam(defaultValue = "movie.created") String routingKey,
            HttpServletRequest req
    ) {
        try {
            String apiKey = (String) req.getAttribute("AUTH_API_KEY");
            if (apiKey == null) {
                return ResponseEntity.status(401).body(Map.of("error","Missing or invalid X-API-KEY"));
            }

            // ===== Validaciones de negocio reusando el Envelope =====
            EventEnvelope envelope = new EventEnvelope();
            envelope.setType(eventDTO.getType());
            envelope.setSource(eventDTO.getSource());
            envelope.setDatacontenttype(eventDTO.getDatacontenttype());
            envelope.setSysDate(eventDTO.getSysDate()); // EventDTO.sysDate es OffsetDateTime
            envelope.setData(mapper.valueToTree(eventDTO.getData()));

           
            
            // ========================================================

            StoredEvent stored = eventService.processIncomingEvent(eventDTO, routingKey, apiKey);

            return ResponseEntity.ok(Map.of(
                "status", "sent_to_queue",
                "routingKey", routingKey,
                "occurredAt", stored.getOccurredAt()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", ex.getMessage()));
        } catch (SecurityException se) {
            String msg = se.getMessage() != null ? se.getMessage() : "Forbidden";
            return ResponseEntity.status(403).body(Map.of("error", msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    // ============================================================
    // 3B. Recibir evento validado (EventEnvelope + reglas negocio)
    // ============================================================
    @PostMapping("/receive-v2")
    public ResponseEntity<?> receiveValidatedEvent(
            @Valid @RequestBody EventEnvelope event,
            @RequestParam(defaultValue = "movie.created") String routingKey,
            HttpServletRequest req
    ) {
        try {
            String apiKey = (String) req.getAttribute("AUTH_API_KEY");
            if (apiKey == null) {
                return ResponseEntity.status(401).body(Map.of("error","Missing or invalid X-API-KEY"));
            }

            // Validaciones de negocio
            biz.validateSkew(event);
            biz.validateTypeSourceConsistency(event);

            // Mapeo a tu EventDTO actual
            EventDTO dto = mapEnvelopeToDTO(event);

            StoredEvent stored = eventService.processIncomingEvent(dto, routingKey, apiKey);

            return ResponseEntity.ok(Map.of(
                "status", "sent_to_queue",
                "routingKey", routingKey,
                "occurredAt", stored.getOccurredAt()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", ex.getMessage()));
        } catch (SecurityException se) {
            String msg = se.getMessage() != null ? se.getMessage() : "Forbidden";
            return ResponseEntity.status(403).body(Map.of("error", msg));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /** Mapeo exacto al formato de tu EventDTO */
    private EventDTO mapEnvelopeToDTO(EventEnvelope ev) {
        EventDTO dto = new EventDTO();

        dto.setType(ev.getType());
        dto.setSource(ev.getSource());
        dto.setDatacontenttype(ev.getDatacontenttype());

        if (ev.getSysDate() != null) {
            dto.setSysDate(ev.getSysDate()); // ya es OffsetDateTime en ambos
        }

        Map<String, Object> dataMap = mapper.convertValue(ev.getData(), Map.class);
        dto.setData(dataMap);

        return dto;
    }

    // ============================================================
    // 4. Debug endpoint
    // ============================================================
    @GetMapping("/_debug/echo")
    public Map<String,Object> echo(HttpServletRequest req) {
        return Map.of(
          "path", req.getRequestURI(),
          "xApiKey", String.valueOf(req.getHeader("X-API-KEY"))
        );
    }

    // ============================================================
    // 5. Estadísticas globales
    // ============================================================
    @Operation(summary = "Obtener estadísticas globales", description = "Métricas de eventos (totales, fallidos, entregados, en cola)")
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(eventService.getGlobalStats());
    }

    // ============================================================
    // 6. Evolución últimas 24h
    // ============================================================
    @Operation(summary = "Evolución de eventos", description = "Cantidad de eventos por hora en las últimas 24h")
    @GetMapping("/evolution")
    public ResponseEntity<?> getEvolution() {
        return ResponseEntity.ok(eventService.getEvolution());
    }

    // ============================================================
    // 7. Eventos por módulo
    // ============================================================
    @Operation(summary = "Eventos por módulo", description = "Devuelve un conteo agrupado por módulo")
    @GetMapping("/per-module")
    public ResponseEntity<?> getEventsPerModule() {
        return ResponseEntity.ok(eventService.getEventsPerModule());
    }
}