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

    /**
     * Escucha SOLO la cola principal "core.all.queue"
     */
    @RabbitListener(queues = CORE_ALL_QUEUE)
    @Transactional
    public void receiveAllEvents(Map<String, Object> message) {
        try {
            System.out.println("\n📥 [ALL QUEUE] Evento recibido: " + message);

            // 🛡️ Evita procesar mensajes reenviados por el propio Core
            if (message.containsKey("_origin") && "core".equals(message.get("_origin"))) {
                System.out.println("🔁 Evento ignorado (reenviado por Core): " + message.get("id"));
                return;
            }

            // 💾 Guarda en DB
            saveEvent(message);

            // 🚀 Redirige el evento a los módulos correspondientes
            routeEventToModules(message);

        } catch (Exception e) {
            System.err.println("❌ Error procesando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Guarda el evento en la base de datos.
     */
    private void saveEvent(Map<String, Object> message) {
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
            System.out.println("✅ Evento guardado correctamente (ID=" + eventId + ")");
        } catch (Exception e) {
            System.err.println("❌ Error guardando evento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Redirige el evento a los módulos que lo necesitan según el tipo.
     */
    private void routeEventToModules(Map<String, Object> message) {
        String eventType = (String) message.getOrDefault("type", "");
        message.put("_origin", "core"); // 🏷️ Marca que fue reenviado por el Core

        try {
            // =============================
            // 🎬 EVENTOS DE PELÍCULAS
            // =============================
            if (eventType.startsWith("movie.")) {
                sendTo(ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
                sendTo(ROUTING_KEY_ANALYTICS, message);        // Analytics
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                sendTo(ROUTING_KEY_SOCIAL, message);           // Social Graph
            }

            // =============================
            // 👤 EVENTOS DE USUARIOS
            // =============================
            else if (eventType.startsWith("user.")) {
                sendTo(ROUTING_KEY_MOVIES, message);           // Movies
                sendTo(ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
                sendTo(ROUTING_KEY_ANALYTICS, message);        // Analytics
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                sendTo(ROUTING_KEY_SOCIAL, message);           // Social Graph
            }

            // =============================
            // ⭐ EVENTOS DE RATINGS/REVIEWS
            // =============================
            else if (eventType.startsWith("rating.") || eventType.startsWith("review.")) {
                sendTo(ROUTING_KEY_ANALYTICS, message);        // Analytics
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                sendTo(ROUTING_KEY_SOCIAL, message);           // Social Graph
            }

            // =============================
            // 🤝 EVENTOS DE SOCIAL GRAPH
            // =============================
            else if (eventType.startsWith("social.")) {
                sendTo(ROUTING_KEY_ANALYTICS, message);        // Analytics
                sendTo(ROUTING_KEY_RECOMMENDATIONS, message);  // Discovery & Recommendations
                sendTo(ROUTING_KEY_RATINGS, message);          // Reviews & Ratings
            }

            System.out.println("📤 Evento reenviado según tipo: " + eventType);

        } catch (Exception e) {
            System.err.println("❌ Error reenviando evento a colas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Envía un mensaje a una cola específica a través del Exchange.
     */
    private void sendTo(String routingKey, Map<String, Object> message) {
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, message);
        System.out.println("➡️ Enviado a cola: " + routingKey);
    }
}
