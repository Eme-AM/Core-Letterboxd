package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public EventController(EventRepository eventRepository, EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    // 🟦 GET lista de eventos (paginada + filtros)
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StoredEvent> events = eventRepository.findAll(pageable);

        // Filtros simples en memoria
        List<StoredEvent> filtered = events.getContent().stream()
                .filter(e -> module == null || e.getSource().toLowerCase().contains(module.toLowerCase()))
                .filter(e -> search == null || e.getPayload().toLowerCase().contains(search.toLowerCase()))
                // status es un campo simulado en este caso
                .filter(e -> status == null || status.equalsIgnoreCase("delivered"))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "page", page,
                "size", size,
                "total", events.getTotalElements(),
                "events", filtered
        ));
    }

    // 🟦 GET detalle de un evento
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventDetail(@PathVariable Long eventId) {
        return eventRepository.findById(eventId)
                .map(event -> ResponseEntity.ok(Map.of(
                        "eventId", event.getEventId(),
                        "type", event.getEventType(),
                        "source", event.getSource(),
                        "status", "Delivered", // simulado
                        "timeline", List.of(
                                Map.of("step", "Received", "time", event.getOccurredAt()),
                                Map.of("step", "Stored", "time", event.getOccurredAt().plusSeconds(2)),
                                Map.of("step", "Delivered", "time", event.getOccurredAt().plusSeconds(5))
                        ),
                        "payload", event.getPayload()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // 🟦 POST recibir un evento nuevo
    @PostMapping("/receive")
    public ResponseEntity<?> receiveEvent(
            @Valid @RequestBody EventDTO eventDTO,
            @RequestParam(defaultValue = "core.routing") String routingKey
    ) {
        try {
            StoredEvent storedEvent = eventService.processIncomingEvent(eventDTO, routingKey);
            eventRepository.save(storedEvent);
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

    // 🟦 Dashboard: métricas globales
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long total = eventRepository.count();
        long delivered = (long) (total * 0.95); // simulado
        long failed = total - delivered;
        long inQueue = 5; // simulado

        // Comparación este mes vs mes pasado
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", total);
        stats.put("delivered", delivered);
        stats.put("failed", failed);
        stats.put("inQueue", inQueue);
        stats.put("thisMonth", thisMonth.toString());
        stats.put("lastMonth", lastMonth.toString());

        return ResponseEntity.ok(stats);
    }

    // 🟦 Dashboard: evolución por hora (últimas 24hs)
    @GetMapping("/evolution")
    public ResponseEntity<?> getEvolution() {
        List<Map<String, Object>> evolution = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 23; i >= 0; i--) {
            evolution.add(Map.of(
                    "hour", now.minusHours(i).getHour(),
                    "count", new Random().nextInt(100) // simulado
            ));
        }

        return ResponseEntity.ok(evolution);
    }

    // 🟦 Dashboard: eventos por módulo
    @GetMapping("/per-module")
    public ResponseEntity<?> getEventsPerModule() {
        Map<String, Long> modules = Map.of(
                "users", 300L,
                "movies", 500L,
                "discovery", 200L,
                "analytics", 700L
        );

        return ResponseEntity.ok(modules);
    }
}