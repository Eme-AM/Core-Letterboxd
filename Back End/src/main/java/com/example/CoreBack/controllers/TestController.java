package com.example.CoreBack.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.service.EventPublisherService;

/**
 * Controlador de prueba
 * 
 * Permite enviar un evento de ejemplo a RabbitMQ para verificar
 * que el outbox y el reintento automático estén funcionando.
 */
@RestController
public class TestController {

    private final EventPublisherService publisherService;

    public TestController(EventPublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> sendTestEvent() {
        // Creamos un evento ficticio (no necesita API Key)
        StoredEvent ev = new StoredEvent();
        ev.setEventType("pelicula.created");
        ev.setSource("/test");
        ev.setContentType("application/json");
        ev.setPayload("{\"ping\":\"ok\"}");
        ev.setOccurredAt(LocalDateTime.now());
        ev.setRoutingKey("event.pelicula");
        ev.setStatus("PENDING");
        ev.setAttempts(0);
        ev.setNextAttemptAt(LocalDateTime.now());
        ev.setMessageId(UUID.randomUUID().toString());

        // Publica el evento (si Rabbit está apagado, queda pendiente)
        publisherService.trySend(ev);

        return ResponseEntity.ok("Evento enviado 🚀");
    }
}
