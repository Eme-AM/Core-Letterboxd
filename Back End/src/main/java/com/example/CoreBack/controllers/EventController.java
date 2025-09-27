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
    return ResponseEntity.ok(
            eventService.getAllEvents(page, size, module, status, search)
    );
}


    
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

    @GetMapping("/stats")
public ResponseEntity<?> getStats() {
    return ResponseEntity.ok(eventService.getGlobalStats());
}

@GetMapping("/evolution")
public ResponseEntity<?> getEvolution() {
    return ResponseEntity.ok(eventService.getEvolution());
}

@GetMapping("/per-module")
public ResponseEntity<?> getEventsPerModule() {
    return ResponseEntity.ok(eventService.getEventsPerModule());
}

}