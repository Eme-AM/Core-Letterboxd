package com.uade.tpo.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uade.tpo.demo.models.events.*;
import com.uade.tpo.demo.models.objects.EventMessage;
import com.uade.tpo.demo.repository.EventMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Service that handles incoming events from RabbitMQ queues
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "letterboxd.event-hub.enabled", havingValue = "true", matchIfMissing = true)
public class EventListenerService {

    private final EventMessageRepository eventMessageRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "movies.events")
    public void handleMovieEvents(MovieEvent event, Message message, Channel channel, 
                                 @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received movie event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the movie event
            processMovieEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Movie event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process movie event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    @RabbitListener(queues = "users.events")
    public void handleUserEvents(UserEvent event, Message message, Channel channel, 
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received user event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the user event
            processUserEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("User event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process user event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    @RabbitListener(queues = "reviews.events")
    public void handleReviewEvents(ReviewEvent event, Message message, Channel channel, 
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received review event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the review event
            processReviewEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Review event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process review event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    @RabbitListener(queues = "social.events")
    public void handleSocialEvents(SocialEvent event, Message message, Channel channel, 
                                  @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received social event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the social event
            processSocialEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Social event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process social event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    @RabbitListener(queues = "discovery.events")
    public void handleDiscoveryEvents(DiscoveryEvent event, Message message, Channel channel, 
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received discovery event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the discovery event
            processDiscoveryEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Discovery event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process discovery event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    @RabbitListener(queues = "analytics.events")
    public void handleAnalyticsEvents(AnalyticsEvent event, Message message, Channel channel, 
                                     @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("Received analytics event: {} - Action: {}", event.getEventId(), event.getAction());
            
            // Process the analytics event
            processAnalyticsEvent(event);
            
            // Update database status
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DELIVERED);
            
            // Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Analytics event processed successfully: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to process analytics event: {} - Error: {}", event.getEventId(), e.getMessage(), e);
            handleEventProcessingError(event, channel, deliveryTag, e);
        }
    }

    // Dead Letter Queue handlers
    @RabbitListener(queues = "movies.failed")
    public void handleFailedMovieEvents(MovieEvent event, Message message, Channel channel, 
                                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.warn("Processing failed movie event: {}", event.getEventId());
            // Store in dead letter for manual review
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.DEAD_LETTER);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Failed to handle dead letter movie event: {}", e.getMessage(), e);
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                log.error("Failed to nack message: {}", ioException.getMessage());
            }
        }
    }

    // Event processing methods
    private void processMovieEvent(MovieEvent event) {
        log.info("Processing movie event - Movie ID: {}, Action: {}", event.getMovieId(), event.getAction());
        // Add specific business logic for movie events
        // For example: update search indices, notify subscribers, etc.
    }

    private void processUserEvent(UserEvent event) {
        log.info("Processing user event - User ID: {}, Action: {}", event.getTargetUserId(), event.getAction());
        // Add specific business logic for user events
        // For example: update user profiles, send welcome emails, etc.
    }

    private void processReviewEvent(ReviewEvent event) {
        log.info("Processing review event - Review ID: {}, Movie ID: {}, Action: {}", 
                event.getReviewId(), event.getMovieId(), event.getAction());
        // Add specific business logic for review events
        // For example: update movie ratings, trigger notifications, etc.
    }

    private void processSocialEvent(SocialEvent event) {
        log.info("Processing social event - Target User: {}, Content: {}, Action: {}", 
                event.getTargetUserId(), event.getContentId(), event.getAction());
        // Add specific business logic for social events
        // For example: update activity feeds, send notifications, etc.
    }

    private void processDiscoveryEvent(DiscoveryEvent event) {
        log.info("Processing discovery event - Action: {}", event.getAction());
        // Add specific business logic for discovery events
        // For example: update recommendation algorithms, trending lists, etc.
    }

    private void processAnalyticsEvent(AnalyticsEvent event) {
        log.info("Processing analytics event - Metric: {}, Action: {}", 
                event.getMetricType(), event.getAction());
        // Add specific business logic for analytics events
        // For example: update dashboards, generate reports, etc.
    }

    // Utility methods
    private void updateEventStatus(String eventId, EventMessage.EventStatus status) {
        try {
            // This is a simplified approach - in a real implementation, you might need
            // to find the event by eventId (which would require adding this field to EventMessage)
            log.info("Updating event {} status to {}", eventId, status);
        } catch (Exception e) {
            log.warn("Failed to update event status: {}", e.getMessage());
        }
    }

    private void handleEventProcessingError(BaseEvent event, Channel channel, long deliveryTag, Exception error) {
        try {
            updateEventStatus(event.getEventId(), EventMessage.EventStatus.FAILED);
            // Reject the message and don't requeue (send to DLX)
            channel.basicNack(deliveryTag, false, false);
        } catch (IOException e) {
            log.error("Failed to nack message: {}", e.getMessage());
        }
    }
}
