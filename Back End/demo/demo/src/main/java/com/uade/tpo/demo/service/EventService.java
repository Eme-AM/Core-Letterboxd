package com.uade.tpo.demo.service;

import com.uade.tpo.demo.models.dto.EventDto;
import com.uade.tpo.demo.models.dto.EventStats;
import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.models.objects.EventMessage.EventStatus;
import com.uade.tpo.demo.repository.EventMessageRepository;
import com.uade.tpo.demo.config.EventConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventMessageRepository eventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Procesa eventos entrantes de otros módulos
     */
    @RabbitListener(queues = EventConfig.INCOMING_EVENTS_QUEUE)
    @Transactional
    public void processIncomingEvent(EventMessage event) {
        try {
            log.info("Processing incoming event: {} from module: {}", 
                    event.getEventType(), event.getSourceModule());
            
            // Validar evento
            validateEvent(event);
            
            // Marcar como procesando
            event.setStatus(EventStatus.PROCESSING);
            eventRepository.save(event);
            
            // Enrutar evento a los módulos correspondientes
            routeEvent(event);
            
            // Marcar como entregado
            event.setStatus(EventStatus.DELIVERED);
            event.setProcessedAt(LocalDateTime.now());
            eventRepository.save(event);
            
            log.info("Successfully processed event: {}", event.getId());
            
        } catch (Exception e) {
            log.error("Error processing event: {}", event.getId(), e);
            handleEventError(event, e);
        }
    }
    
    /**
     * Publica un evento al sistema
     */
    @Transactional
    public EventMessage publishEvent(EventDto eventDto) {
        try {
            // Crear mensaje de evento
            EventMessage event = EventMessage.builder()
                    .eventType(eventDto.getEventType())
                    .sourceModule(eventDto.getSourceModule())
                    .targetModule(eventDto.getTargetModule())
                    .payload(convertPayloadToJson(eventDto.getPayload()))
                    .status(EventStatus.PENDING)
                    .build();
            
            // Persistir evento
            event = eventRepository.save(event);
            
            // Publicar al exchange
            String routingKey = determineRoutingKey(event);
            rabbitTemplate.convertAndSend(EventConfig.LETTERBOXD_EXCHANGE, routingKey, event);
            
            log.info("Published event: {} with routing key: {}", event.getId(), routingKey);
            
            return event;
            
        } catch (Exception e) {
            log.error("Error publishing event", e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
    
    /**
     * Enruta eventos a los módulos correspondientes
     */
    private void routeEvent(EventMessage event) {
        String routingKey = determineRoutingKey(event);
        
        // Si hay un módulo objetivo específico, enrutar solo a ese módulo
        if (event.getTargetModule() != null) {
            String targetRoutingKey = event.getTargetModule() + "." + event.getEventType().toLowerCase();
            rabbitTemplate.convertAndSend(EventConfig.LETTERBOXD_EXCHANGE, targetRoutingKey, event);
            log.info("Routed event {} to target module: {}", event.getId(), event.getTargetModule());
        } else {
            // Broadcast a todos los módulos relevantes
            rabbitTemplate.convertAndSend(EventConfig.LETTERBOXD_EXCHANGE, routingKey, event);
            log.info("Broadcasted event {} with routing key: {}", event.getId(), routingKey);
        }
    }
    
    /**
     * Reintenta eventos fallidos
     */
    @RabbitListener(queues = EventConfig.RETRY_QUEUE)
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void retryFailedEvent(EventMessage event) {
        try {
            log.info("Retrying failed event: {}", event.getId());
            
            event.setStatus(EventStatus.PROCESSING);
            event.setRetryCount(event.getRetryCount() + 1);
            eventRepository.save(event);
            
            // Intentar procesar nuevamente
            processIncomingEvent(event);
            
        } catch (Exception e) {
            log.error("Retry failed for event: {}", event.getId(), e);
            
            if (event.getRetryCount() >= event.getMaxRetries()) {
                // Enviar a Dead Letter Queue
                event.setStatus(EventStatus.DEAD_LETTER);
                event.setErrorMessage(e.getMessage());
                eventRepository.save(event);
                
                rabbitTemplate.convertAndSend(EventConfig.DLQ_QUEUE, event);
                log.warn("Event {} sent to Dead Letter Queue after {} retries", 
                        event.getId(), event.getRetryCount());
            } else {
                // Reintentarlo más tarde
                event.setStatus(EventStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                eventRepository.save(event);
                
                rabbitTemplate.convertAndSend(EventConfig.RETRY_QUEUE, event);
            }
        }
    }
    
    /**
     * Reintento manual de evento
     */
    @Transactional
    public void manualRetry(Long eventId) {
        EventMessage event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        
        if (event.getStatus() == EventStatus.FAILED || event.getStatus() == EventStatus.DEAD_LETTER) {
            event.setStatus(EventStatus.PENDING);
            event.setErrorMessage(null);
            eventRepository.save(event);
            
            // Reencolar para procesamiento
            String routingKey = determineRoutingKey(event);
            rabbitTemplate.convertAndSend(EventConfig.LETTERBOXD_EXCHANGE, routingKey, event);
            
            log.info("Manually retrying event: {}", eventId);
        } else {
            throw new RuntimeException("Event cannot be retried in current status: " + event.getStatus());
        }
    }
    
    /**
     * Búsqueda con filtros para el dashboard
     */
    public Page<EventMessage> searchEvents(String eventType, String sourceModule, String status, 
                                         LocalDateTime from, LocalDateTime to, Pageable pageable) {
        EventStatus eventStatus = status != null ? EventStatus.valueOf(status.toUpperCase()) : null;
        return eventRepository.findWithFilters(eventType, sourceModule, eventStatus, from, to, pageable);
    }
    
    /**
     * Obtiene estadísticas de eventos
     */
    public EventStats getEventStats() {
        return EventStats.builder()
                .totalEvents(eventRepository.count())
                .pendingEvents(eventRepository.countByStatus(EventStatus.PENDING))
                .processedEvents(eventRepository.countByStatus(EventStatus.DELIVERED))
                .failedEvents(eventRepository.countByStatus(EventStatus.FAILED))
                .retryingEvents(eventRepository.countByStatus(EventStatus.PROCESSING))
                .successRate(calculateSuccessRate())
                .averageProcessingTime(calculateAverageProcessingTime())
                .userModuleEvents(eventRepository.countBySourceModule("usuarios"))
                .movieModuleEvents(eventRepository.countBySourceModule("peliculas"))
                .reviewModuleEvents(eventRepository.countBySourceModule("reviews"))
                .socialModuleEvents(eventRepository.countBySourceModule("social"))
                .discoveryModuleEvents(eventRepository.countBySourceModule("discovery"))
                .analyticsModuleEvents(eventRepository.countBySourceModule("analytics"))
                .build();
    }
    
    /**
     * Busca evento por ID
     */
    public EventMessage findById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
    }
    
    // Métodos auxiliares
    
    private void validateEvent(EventMessage event) {
        if (event.getEventType() == null || event.getEventType().trim().isEmpty()) {
            throw new IllegalArgumentException("Event type is required");
        }
        if (event.getSourceModule() == null || event.getSourceModule().trim().isEmpty()) {
            throw new IllegalArgumentException("Source module is required");
        }
        if (event.getPayload() == null) {
            throw new IllegalArgumentException("Event payload is required");
        }
    }
    
    private void handleEventError(EventMessage event, Exception e) {
        event.setStatus(EventStatus.FAILED);
        event.setErrorMessage(e.getMessage());
        event.setRetryCount(event.getRetryCount() + 1);
        eventRepository.save(event);
        
        // Enviar a cola de reintentos si no ha excedido el límite
        if (event.getRetryCount() <= event.getMaxRetries()) {
            rabbitTemplate.convertAndSend(EventConfig.RETRY_QUEUE, event);
        } else {
            rabbitTemplate.convertAndSend(EventConfig.DLQ_QUEUE, event);
        }
    }
    
    private String determineRoutingKey(EventMessage event) {
        String sourceModule = event.getSourceModule().toLowerCase();
        String eventType = event.getEventType().toLowerCase();
        return sourceModule + "." + eventType;
    }
    
    private String convertPayloadToJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }
    
    private double calculateSuccessRate() {
        long total = eventRepository.count();
        if (total == 0) return 100.0;
        
        long successful = eventRepository.countByStatus(EventStatus.DELIVERED);
        return (double) successful / total * 100.0;
    }
    
    private double calculateAverageProcessingTime() {
        Double avgTime = eventRepository.getAverageProcessingTimeByStatus(EventStatus.DELIVERED);
        return avgTime != null ? avgTime / 1000.0 : 0.0; // Convert microseconds to milliseconds
    }
}
