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
        processMessage(message, "ALL");
    }

    @RabbitListener(queues = CORE_USERS_QUEUE)
    public void receiveUserEvents(Map<String, Object> message) {
        processMessage(message, "USERS");
    }

    @RabbitListener(queues = CORE_MOVIES_QUEUE)
    public void receiveMovieEvents(Map<String, Object> message) {
        processMessage(message, "MOVIES");
    }

    @RabbitListener(queues = CORE_RATINGS_QUEUE)
    public void receiveRatingEvents(Map<String, Object> message) {
        processMessage(message, "RATINGS");
    }

    @RabbitListener(queues = CORE_SOCIAL_QUEUE)
    public void receiveSocialEvents(Map<String, Object> message) {
        processMessage(message, "SOCIAL");
    }

    private void processMessage(Map<String, Object> message, String queue) {
        try {
            System.out.println("📩 Evento recibido en [" + queue + "] : " + message);

            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent storedEvent = new StoredEvent(
                    (String) message.getOrDefault("id", "unknown"),
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "1.0",
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
}
