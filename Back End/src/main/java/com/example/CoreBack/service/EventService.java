package com.example.CoreBack.service;

import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisherService publisherService;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository,
                        EventPublisherService publisherService,
                        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.publisherService = publisherService;
        this.objectMapper = objectMapper;
    }

    public StoredEvent processIncomingEvent(@Valid EventDTO eventDTO, String routingKey) {
        try {
            // Construir evento
            StoredEvent storedEvent = new StoredEvent(
                    eventDTO.getId(),
                    eventDTO.getType(),
                    eventDTO.getSource(),
                    eventDTO.getDatacontenttype(),
                    objectMapper.writeValueAsString(eventDTO.getData()),
                    eventDTO.getSysDate() != null ? eventDTO.getSysDate() : LocalDateTime.now()
            );

            // Guardar en DB
            eventRepository.save(storedEvent);

            // Publicar en Rabbit
            publisherService.publish(eventDTO, routingKey);

            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }
}