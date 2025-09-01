package com.example.CoreBack.service;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveMessage(Map<String, Object> message) {
        try {
            System.out.println("📩 Evento recibido: " + message);

            // Convertir Map a JSON String
            String payloadJson = objectMapper.writeValueAsString(message);

            // Guardar en DB
            StoredEvent storedEvent = new StoredEvent(
                    (String) message.getOrDefault("eventId", "unknown"),
                    (String) message.getOrDefault("type", "UNKNOWN"),
                    (String) message.getOrDefault("source", "unknown"),
                    "1.0",
                    payloadJson,
                    java.time.LocalDateTime.now()
            );

            eventRepository.save(storedEvent);
            System.out.println("💾 Evento guardado en DB con ID = " + storedEvent.getId());

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error al procesar evento: " + e.getMessage());
        }
    }
}


