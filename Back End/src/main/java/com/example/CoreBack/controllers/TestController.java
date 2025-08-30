package com.example.CoreBack.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CoreBack.service.EventPublisherService;

@RestController
@RequestMapping("/test")
public class TestController {

    private final EventPublisherService eventPublisher;

    public TestController(EventPublisherService eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public String sendTestEvent() {
        Map<String, Object> event = Map.of(
            "source", "peliculas",
            "type", "PELICULA_ACTUALIZADA",
            "titulo", "Matrix Reloaded"
        );

        eventPublisher.publish(event, "event.pelicula");
        return "Evento enviado 🚀";
    }
}

