package com.example.CoreBack.service;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.EventDTO;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventPublisherTest publisherService;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository,
                        EventPublisherTest publisherService,
                        ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.publisherService = publisherService;
        this.objectMapper = objectMapper;
    }

    public StoredEvent processIncomingEvent(@Valid EventDTO eventDTO, String routingKey) {
        try {
            
            String eventId = eventDTO.getId();
    
            
            String type = eventDTO.getType();
            String source = eventDTO.getSource();
            String contentType = eventDTO.getDatacontenttype();
    
            
            String payloadJson = objectMapper.writeValueAsString(eventDTO.getData());
    
            
            LocalDateTime occurredAt = eventDTO.getSysDate() != null
                    ? eventDTO.getSysDate()
                    : LocalDateTime.now();
    
            
            StoredEvent storedEvent = new StoredEvent(
                    eventId, type, source, contentType, payloadJson, occurredAt
            );
            
    
            
            publisherService.publish(eventDTO, routingKey);
    
            return storedEvent;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento", e);
        }
    }
    
}
