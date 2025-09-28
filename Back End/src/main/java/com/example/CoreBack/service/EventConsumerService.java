package com.example.CoreBack.service;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.example.CoreBack.config.RabbitConfig.*;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    
    @RabbitListener(queues = CORE_ALL_QUEUE)
    public void receiveAllEvents(Map<String, Object> message) {
        try {
            System.out.println(" Evento recibido en [ALL] : " + message);

            String eventId = (String) message.getOrDefault("id", "unknown");

            // Validar duplicados
            if (eventRepository.findAll().stream().anyMatch(e -> e.getEventId().equals(eventId))) {
                System.out.println("⚠️ Evento duplicado ignorado: " + eventId);
                return;
            }

            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent storedEvent = new StoredEvent(
                    eventId,
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "application/json",
                    payloadJson,
                    java.time.LocalDateTime.now()
            );

            eventRepository.save(storedEvent);
            System.out.println(" Evento guardado en DB con ID = " + storedEvent.getId());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(" Error al procesar evento: " + e.getMessage());
        }
    }

    @RabbitListener(queues = CORE_USERS_QUEUE)
    public void receiveUserEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [USERS] : " + message);
    }

    @RabbitListener(queues = CORE_MOVIES_QUEUE)
    public void receiveMovieEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [MOVIES] : " + message);
    }

    @RabbitListener(queues = CORE_RATINGS_QUEUE)
    public void receiveRatingEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [RATINGS] : " + message);
    }

    @RabbitListener(queues = CORE_SOCIAL_QUEUE)
    public void receiveSocialEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [SOCIAL] : " + message);
    }

    @RabbitListener(queues = CORE_ANALYTICS_QUEUE)
    public void receiveAnalyticsEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [ANALYTICS] : " + message);
    }

    @RabbitListener(queues = CORE_RECOMMENDATIONS_QUEUE)
    public void receiveRecommendationsEvents(Map<String, Object> message) {
        System.out.println(" Evento recibido en [RECOMMENDATIONS] : " + message);
    }
}

