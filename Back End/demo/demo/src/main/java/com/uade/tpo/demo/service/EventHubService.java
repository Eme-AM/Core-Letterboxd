package com.uade.tpo.demo.service;

import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.models.objects.EventMessage.EventStatus;
import com.uade.tpo.demo.models.requests.EventFilterRequest;
import com.uade.tpo.demo.models.requests.EventPublishRequest;
import com.uade.tpo.demo.models.responses.EventMessageDTO;
import com.uade.tpo.demo.models.responses.EventStatsDTO;
import com.uade.tpo.demo.repository.EventMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventHubService {

    private final EventMessageRepository eventMessageRepository;

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
        
        // TODO: Send to message queue (RabbitMQ/Kafka) for async processing
        // processEventAsync(savedEvent);
        
        return convertToDTO(savedEvent);
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
