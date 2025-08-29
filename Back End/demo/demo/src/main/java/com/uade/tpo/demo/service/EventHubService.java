package com.uade.tpo.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.demo.models.events.BaseEvent;
import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.models.objects.EventMessage.EventStatus;
import com.uade.tpo.demo.models.requests.EventFilterRequest;
import com.uade.tpo.demo.models.requests.EventPublishRequest;
import com.uade.tpo.demo.models.responses.EventMessageDTO;
import com.uade.tpo.demo.models.responses.EventStatsDTO;
import com.uade.tpo.demo.repository.EventMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class EventHubService {

    private final EventMessageRepository eventMessageRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${letterboxd.event-hub.routing.default-exchange:letterboxd.events}")
    private String defaultExchange;

    @Transactional
    public EventMessageDTO publishEvent(EventPublishRequest request) {
        log.info("Publishing event: {} from module: {}", request.getEventType(), request.getSourceModule());
        
        EventMessage eventMessage = EventMessage.builder()
                .eventType(request.getEventType())
                .sourceModule(request.getSourceModule())
                .targetModule(request.getTargetModule())
                .payload(request.getPayload())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .correlationId(request.getCorrelationId())
                .build();

        EventMessage savedEvent = eventMessageRepository.save(eventMessage);
        log.info("Event published with ID: {}", savedEvent.getId());
        
        // Send to RabbitMQ for async processing
        try {
            String routingKey = determineRoutingKey(request.getEventType(), request.getTargetModule());
            rabbitTemplate.convertAndSend(defaultExchange, routingKey, savedEvent);
            log.info("Event sent to RabbitMQ with routing key: {}", routingKey);
            
            // Mark as processing
            markEventAsProcessing(savedEvent.getId());
        } catch (Exception e) {
            log.error("Failed to send event to RabbitMQ: {}", e.getMessage(), e);
            markEventAsFailed(savedEvent.getId(), e.getMessage());
        }
        
        return convertToDTO(savedEvent);
    }

    /**
     * Publishes a BaseEvent directly to RabbitMQ
     */
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void publishEvent(BaseEvent event) {
        try {
            log.info("Publishing BaseEvent: {} with routing key: {}", event.getEventType(), event.getRoutingKey());
            
            // Store event in database for tracking
            storeBaseEvent(event);
            
            // Send to RabbitMQ
            rabbitTemplate.convertAndSend(defaultExchange, event.getRoutingKey(), event);
            
            log.info("BaseEvent published successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to publish BaseEvent: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Determines routing key based on event type and target module
     */
    private String determineRoutingKey(String eventType, String targetModule) {
        if (targetModule != null && !targetModule.trim().isEmpty()) {
            return targetModule.toLowerCase() + "." + eventType.toLowerCase();
        }
        
        // Default routing based on event type
        String eventTypeLower = eventType.toLowerCase();
        if (eventTypeLower.contains("movie")) {
            return "movies." + eventTypeLower;
        } else if (eventTypeLower.contains("user")) {
            return "users." + eventTypeLower;
        } else if (eventTypeLower.contains("review") || eventTypeLower.contains("rating")) {
            return "reviews." + eventTypeLower;
        } else if (eventTypeLower.contains("social") || eventTypeLower.contains("follow") || eventTypeLower.contains("like")) {
            return "social." + eventTypeLower;
        } else if (eventTypeLower.contains("discovery") || eventTypeLower.contains("search") || eventTypeLower.contains("recommend")) {
            return "discovery." + eventTypeLower;
        } else if (eventTypeLower.contains("analytics") || eventTypeLower.contains("metric")) {
            return "analytics." + eventTypeLower;
        }
        
        return "general." + eventTypeLower;
    }

    /**
     * Stores BaseEvent in database for tracking
     */
    private void storeBaseEvent(BaseEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            EventMessage eventMessage = EventMessage.builder()
                    .eventType(event.getEventType())
                    .sourceModule(event.getSource())
                    .targetModule(determineTargetFromRoutingKey(event.getRoutingKey()))
                    .payload(eventJson)
                    .correlationId(event.getCorrelationId())
                    .status(EventStatus.PROCESSING)
                    .build();

            eventMessageRepository.save(eventMessage);
            log.debug("BaseEvent stored in database: {}", event.getEventId());
            
        } catch (Exception e) {
            log.warn("Failed to store BaseEvent in database: {} - Error: {}", event.getEventId(), e.getMessage());
        }
    }

    /**
     * Determines target module from routing key
     */
    private String determineTargetFromRoutingKey(String routingKey) {
        if (routingKey.startsWith("movies.")) return "MOVIES";
        if (routingKey.startsWith("users.")) return "USERS";
        if (routingKey.startsWith("reviews.")) return "REVIEWS";
        if (routingKey.startsWith("social.")) return "SOCIAL";
        if (routingKey.startsWith("discovery.")) return "DISCOVERY";
        if (routingKey.startsWith("analytics.")) return "ANALYTICS";
        return "UNKNOWN";
    }

    @Transactional
    public void markEventAsProcessing(Long eventId) {
        Optional<EventMessage> eventOptional = eventMessageRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            EventMessage event = eventOptional.get();
            event.setStatus(EventStatus.PROCESSING);
            event.setProcessedAt(LocalDateTime.now());
            eventMessageRepository.save(event);
            log.info("Event {} marked as processing", eventId);
        }
    }

    @Transactional
    public void markEventAsDelivered(Long eventId) {
        Optional<EventMessage> eventOptional = eventMessageRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            EventMessage event = eventOptional.get();
            event.setStatus(EventStatus.DELIVERED);
            event.setProcessedAt(LocalDateTime.now());
            eventMessageRepository.save(event);
            log.info("Event {} marked as delivered", eventId);
        }
    }

    @Transactional
    public void markEventAsFailed(Long eventId, String errorMessage) {
        Optional<EventMessage> eventOptional = eventMessageRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            EventMessage event = eventOptional.get();
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(errorMessage);
            
            if (event.getRetryCount() >= event.getMaxRetries()) {
                event.setStatus(EventStatus.DEAD_LETTER);
                log.warn("Event {} moved to dead letter queue after {} retries", eventId, event.getRetryCount());
            } else {
                event.setStatus(EventStatus.FAILED);
                log.warn("Event {} failed, retry count: {}", eventId, event.getRetryCount());
            }
            
            eventMessageRepository.save(event);
        }
    }

    public Page<EventMessageDTO> getEventsByFilters(EventFilterRequest request) {
        log.info("Filtering events with filters: {}", request);
        
        LocalDateTime fromDate = request.getFromDate();
        LocalDateTime toDate = request.getToDate();
        EventStatus status = request.getStatus() != null ? EventStatus.valueOf(request.getStatus().toUpperCase()) : null;
        
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(request.getSortDir()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC, 
            request.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        Page<EventMessage> events = eventMessageRepository.findByFilters(
            request.getEventType(),
            request.getSourceModule(),
            request.getTargetModule(),
            status,
            request.getCorrelationId(),
            fromDate,
            toDate,
            pageable
        );
        
        return events.map(this::convertToDTO);
    }

    public List<EventMessageDTO> getPendingEvents() {
        List<EventMessage> pendingEvents = eventMessageRepository.findByStatus(EventStatus.PENDING);
        return pendingEvents.stream().map(this::convertToDTO).toList();
    }

    public List<EventMessageDTO> getFailedEventsForRetry() {
        List<EventMessage> failedEvents = eventMessageRepository.findByStatusAndRetryCountLessThan(
            EventStatus.FAILED, 3
        );
        return failedEvents.stream().map(this::convertToDTO).toList();
    }

    public EventStatsDTO getEventStatistics() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
        
        return EventStatsDTO.builder()
                .totalEvents(eventMessageRepository.count())
                .pendingEvents(eventMessageRepository.countByStatus(EventStatus.PENDING))
                .processingEvents(eventMessageRepository.countByStatus(EventStatus.PROCESSING))
                .deliveredEvents(eventMessageRepository.countByStatus(EventStatus.DELIVERED))
                .failedEvents(eventMessageRepository.countByStatus(EventStatus.FAILED))
                .deadLetterEvents(eventMessageRepository.countByStatus(EventStatus.DEAD_LETTER))
                .moviesModuleEvents(eventMessageRepository.countBySourceModule("MOVIES"))
                .usersModuleEvents(eventMessageRepository.countBySourceModule("USERS"))
                .reviewsModuleEvents(eventMessageRepository.countBySourceModule("REVIEWS"))
                .socialModuleEvents(eventMessageRepository.countBySourceModule("SOCIAL"))
                .discoveryModuleEvents(eventMessageRepository.countBySourceModule("DISCOVERY"))
                .analyticsModuleEvents(eventMessageRepository.countBySourceModule("ANALYTICS"))
                .eventsLast24Hours(eventMessageRepository.countEventsSince(last24Hours))
                .eventsLastHour(eventMessageRepository.countEventsSince(lastHour))
                .averageProcessingTime(eventMessageRepository.getAverageProcessingTimeInSeconds())
                .build();
    }

    private EventMessageDTO convertToDTO(EventMessage event) {
        return EventMessageDTO.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .sourceModule(event.getSourceModule())
                .targetModule(event.getTargetModule())
                .payload(event.getPayload())
                .status(event.getStatus().toString())
                .retryCount(event.getRetryCount())
                .maxRetries(event.getMaxRetries())
                .priority(event.getPriority())
                .correlationId(event.getCorrelationId())
                .createdAt(event.getCreatedAt())
                .processedAt(event.getProcessedAt())
                .errorMessage(event.getErrorMessage())
                .build();
    }

}
