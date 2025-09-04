package com.example.CoreBack.controllers;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.example.CoreBack.service.EventService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;

    public EventController(EventRepository eventRepository,EventService eventService) {
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    
    @GetMapping
    public List<StoredEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping("/receive")
    public ResponseEntity<?> receiveEvent(@Valid @RequestBody EventDTO eventDTO,
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


