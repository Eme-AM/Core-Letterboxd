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
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
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

    // Procesa y publica evento
    public StoredEvent processIncomingEvent(@Valid EventDTO eventDTO, String routingKey) {
        try {
            String type = eventDTO.getType();
            String source = eventDTO.getSource();
            String contentType = eventDTO.getDatacontenttype();

            String payloadJson = objectMapper.writeValueAsString(eventDTO.getData());

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sysDate = eventDTO.getSysDate();

            LocalDateTime occurredAt;
            if (sysDate != null &&
                !sysDate.isAfter(now.plusMinutes(5)) &&
                !sysDate.isBefore(now.minusDays(1))) {
                occurredAt = sysDate;
            } else {
                occurredAt = now;
            }

            StoredEvent storedEvent = new StoredEvent(
                    type,
                    source,
                    contentType,
                    payloadJson,
                    occurredAt
            );

            storedEvent.setStatus("InQueue");
            publisherService.publish(eventDTO, routingKey);

            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }

    // 🔍 Listar con filtros
    public Map<String, Object> getAllEvents(int page, int size, String module, String status, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));
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

    // 📊 Estadísticas globales
    public Map<String, Object> getGlobalStats() {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        LocalDate startOfThisMonth = thisMonth.atDay(1);
        LocalDate startOfNextMonth = thisMonth.plusMonths(1).atDay(1);
        LocalDate startOfLastMonth = lastMonth.atDay(1);

        List<StoredEvent> allEvents = eventRepository.findAll();

        Predicate<StoredEvent> inThisMonth = e -> {
            LocalDate date = e.getOccurredAt().toLocalDate();
            return !date.isBefore(startOfThisMonth) && date.isBefore(startOfNextMonth);
        };
        Predicate<StoredEvent> inLastMonth = e -> {
            LocalDate date = e.getOccurredAt().toLocalDate();
            return !date.isBefore(startOfLastMonth) && date.isBefore(startOfThisMonth);
        };

        long totalThisMonth = allEvents.stream().filter(inThisMonth).count();
        long totalLastMonth = allEvents.stream().filter(inLastMonth).count();

        long deliveredThisMonth = allEvents.stream().filter(e -> "Delivered".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
        long deliveredLastMonth = allEvents.stream().filter(e -> "Delivered".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

        long failedThisMonth = allEvents.stream().filter(e -> "Failed".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
        long failedLastMonth = allEvents.stream().filter(e -> "Failed".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

        long inQueueThisMonth = allEvents.stream().filter(e -> "InQueue".equalsIgnoreCase(e.getStatus()) && inThisMonth.test(e)).count();
        long inQueueLastMonth = allEvents.stream().filter(e -> "InQueue".equalsIgnoreCase(e.getStatus()) && inLastMonth.test(e)).count();

        BiFunction<Long, Long, Integer> calcChange = (current, previous) -> {
            long difference = current - previous;
            long base = Math.max(previous, 1);
            return (int) ((difference * 100) / base);
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

    // 📈 Evolución últimas 24h
    public List<Map<String, Object>> getEvolution() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusHours(23).truncatedTo(ChronoUnit.HOURS);

        List<StoredEvent> last24hEvents = eventRepository.findAll().stream()
                .filter(e -> !e.getOccurredAt().isBefore(start))
                .toList();

        Map<LocalDateTime, Long> counts = last24hEvents.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getOccurredAt().truncatedTo(ChronoUnit.HOURS),
                        Collectors.counting()
                ));

        List<Map<String, Object>> evolution = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime hour = start.plusHours(i);
            long count = counts.getOrDefault(hour, 0L);
            evolution.add(Map.of(
                    "hour", hour.getHour(),
                    "count", count
            ));
        }

        return evolution;
    }

    // 🧩 Agrupación por módulo
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