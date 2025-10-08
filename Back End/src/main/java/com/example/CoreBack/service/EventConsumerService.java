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

import static com.example.CoreBack.config.RabbitConfig.CORE_ALL_QUEUE;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        System.out.println("===========================================");
        System.out.println("🎧 EventConsumerService INICIADO");
        System.out.println("📡 Escuchando solo la cola principal: " + CORE_ALL_QUEUE);
        System.out.println("===========================================");
    }

    /**
     * 📨 Escucha la cola principal (core.all.queue)
     * y guarda cada evento recibido en la base de datos.
     */
    @RabbitListener(queues = CORE_ALL_QUEUE)
    @Transactional
    public void receiveAllEvents(Map<String, Object> message) {
        System.out.println("📥 [ALL QUEUE] Evento recibido: " + message);

        // Validación básica
        if (message == null || message.isEmpty()) {
            System.out.println("⚠️ Evento vacío recibido, ignorado.");
            return;
        }

        try {
            String eventId = (String) message.getOrDefault("id", "unknown");
            String eventType = (String) message.getOrDefault("type", "UNKNOWN");
            String eventSource = (String) message.getOrDefault("source", "unknown");

            String payloadJson = objectMapper.writeValueAsString(message);

            StoredEvent storedEvent = new StoredEvent(
                    eventId,
                    eventType,
                    eventSource,
                    "application/json",
                    payloadJson,
                    LocalDateTime.now()
            );

            eventRepository.save(storedEvent);
            System.out.println("✅ Evento guardado correctamente (type=" + eventType + ", id=" + eventId + ")");

        } catch (Exception e) {
            System.err.println("❌ Error procesando evento recibido: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
