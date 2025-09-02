package com.example.CoreBack.controllers;

import com.example.CoreBack.model.EventMessage;
import com.example.CoreBack.service.EventPublisherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    private final EventPublisherService eventPublisher;

    public TestController(EventPublisherService eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/test")
    public String sendTestEvent() {
        EventMessage event = new EventMessage();
        event.setSource("peliculas");
        event.setEventType("PELICULA_ACTUALIZADA");
        event.setPayload(Map.of(
                "titulo", "Rapidos y Furiosos",
                "año", 2003
        ));

        eventPublisher.publish(event, "event.pelicula");
        return "Evento enviado ";
    }
}


