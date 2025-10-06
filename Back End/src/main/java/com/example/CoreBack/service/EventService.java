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
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
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
    
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sysDate = eventDTO.getSysDate();
    
            LocalDateTime occurredAt;
            if (sysDate != null &&
                !sysDate.isAfter(now.plusMinutes(5)) &&   // no en el futuro más de 5 min
                !sysDate.isBefore(now.minusDays(1))) {    // no más de 1 día viejo
                occurredAt = sysDate;
            } else {
                occurredAt = now;
            }
    
            StoredEvent storedEvent = new StoredEvent(
                    eventId,
                    type,
                    source,
                    contentType,
                    payloadJson,
                    occurredAt
            );
    
            
            publisherService.publish(eventDTO, routingKey);
    
            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }
    


    // Paginación y filtros
    public Map<String, Object> getAllEvents(int page, int size, String module, String status, String search) {
    // Ordena descendente por fecha (últimos eventos primero)
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));
    Page<StoredEvent> events = eventRepository.findAll(pageable);

    // Filtros aplicados en memoria (pueden optimizarse luego con queries dinámicas)
    List<StoredEvent> filtered = events.getContent().stream()
            .filter(e -> module == null || e.getSource().toLowerCase().contains(module.toLowerCase()))
            .filter(e -> search == null || e.getPayload().toLowerCase().contains(search.toLowerCase()))
            // ⚠️ status sigue siendo simulado, ya que no está en la entidad
            .filter(e -> status == null || status.equalsIgnoreCase("delivered"))
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
    LocalDateTime start = now.minusHours(23).truncatedTo(ChronoUnit.HOURS); // 23 horas atrás

    // Traemos solo los eventos de las últimas 24h
    List<StoredEvent> last24hEvents = eventRepository.findAll().stream()
            .filter(e -> !e.getOccurredAt().isBefore(start)) // >= start
            .toList();

    // --- DEBUG: print de todos los eventos capturados ---
    System.out.println("Eventos en las últimas 24h:");
    last24hEvents.forEach(e ->
            System.out.println("ID: " + e.getEventId() + " | OccurredAt: " + e.getOccurredAt())
    );

    // Agrupamos por fecha+hora truncada
    Map<LocalDateTime, Long> counts = last24hEvents.stream()
            .collect(Collectors.groupingBy(
                    e -> e.getOccurredAt().truncatedTo(ChronoUnit.HOURS),
                    Collectors.counting()
            ));

    // Generamos la lista de evolución
    List<Map<String, Object>> evolution = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
        LocalDateTime hour = start.plusHours(i);
        long count = counts.getOrDefault(hour, 0L);

        // --- DEBUG: print de cada hora y su conteo ---
        System.out.println("Hora: " + hour + " | Count: " + count);

        evolution.add(Map.of(
                "hour", hour.getHour(),
                "count", count
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