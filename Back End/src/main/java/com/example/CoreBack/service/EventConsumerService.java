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
        System.out.println("🎧 EventConsumerService STARTED");
        System.out.println("📡 Listening to main queue: " + CORE_ALL_QUEUE);
        System.out.println("===========================================");
    }

    /**
     * 🎯 Escucha la cola principal "core.all.queue"
     */
    @RabbitListener(queues = CORE_ALL_QUEUE)
    @Transactional
    public void receiveAllEvents(Map<String, Object> message) {
        try {
            System.out.println("\n📥 [ALL QUEUE] Event received: " + message);

            // 🛡️ Evita procesar mensajes reenviados por el propio Core
            if (message.containsKey("_origin") && "core".equals(message.get("_origin"))) {
                System.out.println("🔁 Skipping self-forwarded event: " + message.get("id"));
                return;
            }

            // 💾 Guarda evento como "InQueue"
            StoredEvent storedEvent = saveEvent(message, "InQueue");

            // 🚀 Redirige a otros módulos
            routeEventToModules(message);

            // ✅ Actualiza estado a "Delivered"
            storedEvent.setStatus("Delivered");
            eventRepository.save(storedEvent);
            System.out.println("✅ Event delivered successfully: " + storedEvent.getEventId());

        } catch (Exception e) {
            System.err.println("❌ Error while processing event: " + e.getMessage());
            e.printStackTrace();

            // ⚠️ Guarda un evento fallido
            saveFailedEvent(e);
        }
    }

    /**
     * 💾 Guarda evento con estado inicial (InQueue o Delivered)
     */
    private StoredEvent saveEvent(Map<String, Object> message, String status) {
        try {
            String eventId = (String) message.getOrDefault("id", "unknown");
            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent event = new StoredEvent(
                    eventId,
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "application/json",
                    payloadJson,
                    LocalDateTime.now()
            );

            event.setStatus(status);
            eventRepository.save(event);
            System.out.println("💾 Event saved with status: " + status + " (ID=" + eventId + ")");
            return event;

        } catch (Exception e) {
            System.err.println("❌ Error saving event: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving event", e);
        }
    }

    /**
     * ⚠️ Guarda un evento con estado "Failed"
     */
    private void saveFailedEvent(Exception e) {
        try {
            StoredEvent failed = new StoredEvent(
                    "unknown",
                    "ERROR",
                    "unknown",
                    "application/json",
                    e.getMessage(),
                    LocalDateTime.now()
            );
            failed.setStatus("Failed");
            eventRepository.save(failed);
            System.err.println("⚠️ Event saved with status: Failed");
        } catch (Exception ex) {
            System.err.println("💥 Could not save Failed event: " + ex.getMessage());
        }
    }

    /**
     * 🚀 Redirige el evento a los módulos correspondientes según tipo
     */
    private void routeEventToModules(Map<String, Object> message) {
        String eventType = (String) message.getOrDefault("type", "");
        message.put("_origin", "core");

        try {
            if (eventType.startsWith("movie.")) {
                sendTo(ROUTING_KEY_RATINGS, message);
                sendTo(ROUTING_KEY_ANALYTICS, message);
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);
                sendTo(ROUTING_KEY_SOCIAL, message);
            } else if (eventType.startsWith("user.")) {
                sendTo(ROUTING_KEY_MOVIES, message);
                sendTo(ROUTING_KEY_RATINGS, message);
                sendTo(ROUTING_KEY_ANALYTICS, message);
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);
                sendTo(ROUTING_KEY_SOCIAL, message);
            } else if (eventType.startsWith("rating.") || eventType.startsWith("review.")) {
                sendTo(ROUTING_KEY_ANALYTICS, message);
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);
                sendTo(ROUTING_KEY_SOCIAL, message);
            } else if (eventType.startsWith("social.")) {
                sendTo(ROUTING_KEY_ANALYTICS, message);
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);
                sendTo(ROUTING_KEY_RATINGS, message);
            }

            System.out.println("📤 Event routed successfully: " + eventType);

        } catch (Exception e) {
            System.err.println("❌ Error routing event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 📡 Envía un mensaje a una cola específica a través del Exchange
     */
    private void sendTo(String routingKey, Map<String, Object> message) {
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message);
        System.out.println("➡️ Sent to queue: " + routingKey);
    }
}