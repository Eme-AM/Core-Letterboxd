package com.example.CoreBack.service;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisherTest publisherService;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository,
                        EventPublisherTest publisherService,
                        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.publisherService = publisherService;
        this.objectMapper = objectMapper;
    }

    public StoredEvent processIncomingEvent(@Valid EventDTO eventDTO, String routingKey) {
        try {
            
            String eventId = eventDTO.getId();
    
            
            String type = eventDTO.getType();
            String source = eventDTO.getSource();
            String contentType = eventDTO.getDatacontenttype();
    
            
            String payloadJson = objectMapper.writeValueAsString(eventDTO.getData());
    
            
            LocalDateTime occurredAt = eventDTO.getSysDate() != null
                    ? eventDTO.getSysDate()
                    : LocalDateTime.now();
    
            
            StoredEvent storedEvent = new StoredEvent(
                    eventId, type, source, contentType, payloadJson, occurredAt
            );
            
    
            
            publisherService.publish(eventDTO, routingKey);
    
            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }

    public Map<String, Object> getAllEvents(int page, int size, String module, String status, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StoredEvent> events = eventRepository.findAll(pageable);

        // Filtros aplicados en memoria (si querés performance, se pueden mover a query en repo)
        List<StoredEvent> filtered = events.getContent().stream()
                .filter(e -> module == null || e.getSource().toLowerCase().contains(module.toLowerCase()))
                .filter(e -> search == null || e.getPayload().toLowerCase().contains(search.toLowerCase()))
                // ⚠️ status todavía es simulado porque no lo tenemos en StoredEvent
                .filter(e -> status == null || status.equalsIgnoreCase("delivered"))
                .collect(Collectors.toList());

        return Map.of(
                "page", page,
                "size", size,
                "total", events.getTotalElements(),
                "events", filtered
        );
    }

    public Map<String, Object> getGlobalStats() {
        long total = eventRepository.count();

        // Si querés manejar un campo real "status" deberías agregarlo a StoredEvent.
        long delivered = (long) (total * 0.95); // simulado por ahora
        long failed = total - delivered;
        long inQueue = 5; // simulado

        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", total);
        stats.put("delivered", delivered);
        stats.put("failed", failed);
        stats.put("inQueue", inQueue);
        stats.put("thisMonth", thisMonth.toString());
        stats.put("lastMonth", lastMonth.toString());

        return stats;
    }

    
    public List<Map<String, Object>> getEvolution() {
        LocalDateTime now = LocalDateTime.now();

        List<StoredEvent> last24hEvents = eventRepository.findAll().stream()
                .filter(e -> e.getOccurredAt().isAfter(now.minusHours(24)))
                .toList();

        Map<Integer, Long> counts = last24hEvents.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getOccurredAt().getHour(),
                        Collectors.counting()
                ));

        List<Map<String, Object>> evolution = new ArrayList<>();
        for (int i = 23; i >= 0; i--) {
            int hour = now.minusHours(i).getHour();
            evolution.add(Map.of(
                    "hour", hour,
                    "count", counts.getOrDefault(hour, 0L)
            ));
        }

        return evolution;
    }

   
    public Map<String, Long> getEventsPerModule() {
        return eventRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> {
                            String source = e.getSource().toLowerCase();
                            if (source.contains("user")) return "users";
                            if (source.contains("movie")) return "movies";
                            if (source.contains("discovery")) return "discovery";
                            if (source.contains("analytics")) return "analytics";
                            return "others";
                        },
                        Collectors.counting()
                ));
    }
    
}
