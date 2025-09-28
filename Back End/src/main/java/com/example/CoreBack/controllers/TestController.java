package com.example.CoreBack.controllers;

import com.example.CoreBack.model.EventMessage;
import com.example.CoreBack.service.EventPublisherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Map;

@RestController
public class TestController {

    private final EventPublisherService eventPublisher;

    public TestController(EventPublisherService eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Operation(
        summary = "Enviar evento de prueba",
        description = "Publica un evento de prueba con datos simulados en la cola de mensajería.",
        tags = { "Test" }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento de prueba enviado correctamente."),
        @ApiResponse(responseCode = "500", description = "Error al enviar el evento de prueba.")
    })
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
        return "Evento enviado 🚀";
    }
}
