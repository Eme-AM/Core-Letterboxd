package com.example.CoreBack.service;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static com.example.CoreBack.config.RabbitConfig.*;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final AmqpTemplate rabbitTemplate;

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper, AmqpTemplate rabbitTemplate) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        System.out.println("===========================================");
        System.out.println("🎧 EventConsumerService INICIADO");
        System.out.println("📡 Escuchando solo la cola principal: " + CORE_ALL_QUEUE);
        System.out.println("===========================================");
    }

    @RabbitListener(queues = CORE_ALL_QUEUE)
    @Transactional
    public void receiveAllEvents(Map<String, Object> message) {
        System.out.println("📥 [ALL QUEUE] ===== MENSAJE RECIBIDO =====");
        System.out.println("📥 Contenido: " + message);

        saveEvent(message, "ALL");
        routeEventToModules(message);
    }

    private void saveEvent(Map<String, Object> message, String queueType) {
        try {
            String eventId = (String) message.getOrDefault("id", "unknown");
            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent storedEvent = new StoredEvent(
                    eventId,
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "application/json",
                    payloadJson,
                    LocalDateTime.now()
            );

            eventRepository.save(storedEvent);
            System.out.println("✅ [" + queueType + "] Evento guardado correctamente en DB");

        } catch (Exception e) {
            System.err.println("❌ [" + queueType + "] ERROR guardando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void routeEventToModules(Map<String, Object> message) {
        String eventType = (String) message.getOrDefault("type", "");

        try {
            // =============================
            // 🎬 EVENTOS DE PELÍCULAS
            // =============================
            if (eventType.startsWith("movie.")) {
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_ANALYTICS, message);        // Analytics
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SOCIAL, message);           //SocialGraph
            }

            // =============================
            // 👤 EVENTOS DE USUARIOS
            // =============================
            else if (eventType.startsWith("user.")) {
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_MOVIES, message);           // MOVIE
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_ANALYTICS, message);        // Analytics
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SOCIAL, message);           //SocialGraph
            }

            // =============================
            // ⭐ EVENTOS DE RATINGS/REVIEWS
            // =============================
            else if (eventType.startsWith("rating.") || eventType.startsWith("review.")) {
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_ANALYTICS, message);        // Analytics
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SOCIAL, message);           //SocialGraph
            }

            // =============================
            // 🤝 EVENTOS DE SOCIAL GRAPH
            // =============================
            else if (eventType.startsWith("social.")) {
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_ANALYTICS, message);        // Analytics
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
            }

            

            System.out.println("📤 Evento reenviado según tipo: " + eventType);

        } catch (Exception e) {
            System.err.println("❌ Error reenviando evento a colas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
