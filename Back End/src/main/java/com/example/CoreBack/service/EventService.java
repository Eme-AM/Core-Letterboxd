package com.example.CoreBack.service;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.function.Predicate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisherService publisherService;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository,
                        EventPublisherService publisherService,
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
            storedEvent.setStatus("RECEIVED");
            eventRepository.save(storedEvent);

            publisherService.publish(eventDTO, routingKey);

            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }

    // Paginación y filtros
    public Map<String, Object> getAllEvents(int page, int size, String module, String status, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StoredEvent> events = eventRepository.findAll(pageable);

        List<StoredEvent> filtered = events.getContent().stream()
                .filter(e -> module == null || e.getSource().toLowerCase().contains(module.toLowerCase()))
                .filter(e -> search == null || e.getPayload().toLowerCase().contains(search.toLowerCase()))
                .filter(e -> status == null || e.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());

        return Map.of(
                "page", page,
                "size", size,
                "total", events.getTotalElements(),
                "events", filtered
        );
    }

    // Estadísticas globales reales
    public Map<String, Object> getGlobalStats() {
    YearMonth thisMonth = YearMonth.now();
    YearMonth lastMonth = thisMonth.minusMonths(1);

    LocalDate startOfThisMonth = thisMonth.atDay(1);
    LocalDate startOfNextMonth = thisMonth.plusMonths(1).atDay(1);
    LocalDate startOfLastMonth = lastMonth.atDay(1);
    LocalDate startOfThisMonthForLast = thisMonth.atDay(1); // fin del mes pasado

    List<StoredEvent> allEvents = eventRepository.findAll();

    Predicate<StoredEvent> inThisMonth = e -> {
        LocalDate date = e.getOccurredAt().toLocalDate();
        return !date.isBefore(startOfThisMonth) && date.isBefore(startOfNextMonth);
    };
    Predicate<StoredEvent> inLastMonth = e -> {
        LocalDate date = e.getOccurredAt().toLocalDate();
        return !date.isBefore(startOfLastMonth) && date.isBefore(startOfThisMonthForLast);
    };

    long totalThisMonth = allEvents.stream().filter(inThisMonth).count();
    long totalLastMonth = allEvents.stream().filter(inLastMonth).count();

    long deliveredThisMonth = allEvents.stream().filter(e -> "DELIVERED".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
    long deliveredLastMonth = allEvents.stream().filter(e -> "DELIVERED".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

    long failedThisMonth = allEvents.stream().filter(e -> "FAILED".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
    long failedLastMonth = allEvents.stream().filter(e -> "FAILED".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

    long inQueueThisMonth = allEvents.stream().filter(e -> "RECEIVED".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
    long inQueueLastMonth = allEvents.stream().filter(e -> "RECEIVED".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

    BiFunction<Long, Long, Integer> calcChange = (current, previous) -> {
    long difference = current - previous;            // diferencia absoluta
    long base = Math.max(previous, 1);              // usamos 1 si el mes anterior fue 0 para evitar división por cero
    int percentage = (int)((difference * 100) / base);  // cálculo del porcentaje
    return percentage;
};


    return Map.ofEntries(
        Map.entry("thisMonth", thisMonth.toString()),
        Map.entry("lastMonth", lastMonth.toString()),
        Map.entry("totalEvents", totalThisMonth),
        Map.entry("totalEventsLastMonth", totalLastMonth),
        Map.entry("totalChange", calcChange.apply(totalThisMonth, totalLastMonth)),
        Map.entry("delivered", deliveredThisMonth),
        Map.entry("deliveredChange", calcChange.apply(deliveredThisMonth, deliveredLastMonth)),
        Map.entry("failed", failedThisMonth),
        Map.entry("failedChange", calcChange.apply(failedThisMonth, failedLastMonth)),
        Map.entry("inQueue", inQueueThisMonth),
        Map.entry("inQueueChange", calcChange.apply(inQueueThisMonth, inQueueLastMonth))
    );
}


    // Evolución de eventos (últimas 24h)
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

    // Agrupación por módulo
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