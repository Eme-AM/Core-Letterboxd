package com.example.CoreBack.service;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
        System.out.println("🔧 EventConsumerService CONSTRUCTOR llamado");
    }

    @PostConstruct
    public void init() {
        System.out.println("===========================================");
        System.out.println("🎧 EventConsumerService INICIADO");
        System.out.println("📡 Listeners configurados para:");
        System.out.println("   - " + CORE_ALL_QUEUE);
        System.out.println("   - " + CORE_MOVIES_QUEUE);
        System.out.println("   - " + CORE_USERS_QUEUE);
        System.out.println("   - " + CORE_RATINGS_QUEUE);
        System.out.println("   - " + CORE_SOCIAL_QUEUE);
        System.out.println("   - " + CORE_ANALYTICS_QUEUE);
        System.out.println("   - " + CORE_RECOMMENDATIONS_QUEUE);
        System.out.println("===========================================");
    }

    @RabbitListener(queues = CORE_ALL_QUEUE)
    @Transactional
    public void receiveAllEvents(Map<String, Object> message) {
        System.out.println("📥 [ALL QUEUE] ===== MENSAJE RECIBIDO ===== ");
        System.out.println("📥 [ALL QUEUE] Contenido: " + message);
        saveEvent(message, "ALL");
    }

    @RabbitListener(queues = CORE_MOVIES_QUEUE)
    @Transactional
    public void receiveMovieEvents(Map<String, Object> message) {
        System.out.println("🎬 [MOVIES QUEUE] ===== MENSAJE RECIBIDO ===== ");
        System.out.println("🎬 [MOVIES QUEUE] Contenido: " + message);
        saveEvent(message, "MOVIES");
    }

    @RabbitListener(queues = CORE_USERS_QUEUE)
    @Transactional
    public void receiveUserEvents(Map<String, Object> message) {
        System.out.println("👤 [USERS QUEUE] ===== MENSAJE RECIBIDO =====");
        System.out.println("👤 [USERS QUEUE] Contenido: " + message);
        saveEvent(message, "USERS");
    }

    @RabbitListener(queues = CORE_RATINGS_QUEUE)
    @Transactional
    public void receiveRatingEvents(Map<String, Object> message) {
        System.out.println("⭐ [RATINGS QUEUE] ===== MENSAJE RECIBIDO =====");
        saveEvent(message, "RATINGS");
    }

    @RabbitListener(queues = CORE_SOCIAL_QUEUE)
    @Transactional
    public void receiveSocialEvents(Map<String, Object> message) {
        System.out.println("🤝 [SOCIAL QUEUE] ===== MENSAJE RECIBIDO =====");
        saveEvent(message, "SOCIAL");
    }

    @RabbitListener(queues = CORE_ANALYTICS_QUEUE)
    @Transactional
    public void receiveAnalyticsEvents(Map<String, Object> message) {
        System.out.println("📊 [ANALYTICS QUEUE] ===== MENSAJE RECIBIDO =====");
        saveEvent(message, "ANALYTICS");
    }

    @RabbitListener(queues = CORE_RECOMMENDATIONS_QUEUE)
    @Transactional
    public void receiveRecommendationsEvents(Map<String, Object> message) {
        System.out.println("💡 [RECOMMENDATIONS QUEUE] ===== MENSAJE RECIBIDO =====");
        saveEvent(message, "RECOMMENDATIONS");
    }

    private void saveEvent(Map<String, Object> message, String queueType) {
        try {
            String eventId = (String) message.getOrDefault("id", "unknown");
            
            System.out.println("💾 [" + queueType + "] Guardando evento ID: " + eventId);

            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent storedEvent = new StoredEvent(
                    eventId,
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "application/json",
                    payloadJson,
                    LocalDateTime.now()
            );

            StoredEvent saved = eventRepository.save(storedEvent);
            System.out.println("✅ [" + queueType + "] Evento guardado con ID DB = " + saved.getId());

        } catch (Exception e) {
            System.err.println("❌ [" + queueType + "] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}