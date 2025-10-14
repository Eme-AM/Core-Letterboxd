package com.example.CoreBack.service;

import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static com.example.CoreBack.config.RabbitConfig.CORE_ALL_QUEUE;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventConsumerService(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = CORE_ALL_QUEUE, ackMode = "MANUAL")
    @Transactional
    public void receiveAllEvents(Map<String, Object> message, Channel channel, Message amqpMessage) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

        try {
            System.out.println("📥 [ALL QUEUE] Event received: " + message);

            if (message == null || message.isEmpty()) {
                System.out.println("⚠️ Empty event received, ignored.");
                channel.basicAck(deliveryTag, false);
                return;
            }

            // Extract event data
            String eventId = (String) message.getOrDefault("id", "unknown");
            String eventType = (String) message.getOrDefault("type", "UNKNOWN");
            String eventSource = (String) message.getOrDefault("source", "unknown");

            String payloadJson = objectMapper.writeValueAsString(message);

            // Build and store event
            StoredEvent storedEvent = new StoredEvent(
                    eventType,
                    eventSource,
                    "application/json",
                    payloadJson,
                    LocalDateTime.now()
            );

            storedEvent.setEventId(eventId);
            storedEvent.setStatus("Delivered"); // 👈 Cambia de "InQueue" → "Delivered"

            eventRepository.save(storedEvent);

            System.out.println("✅ Event saved with status = Delivered (type=" + eventType + ", id=" + eventId + ")");

            // Confirm the message to RabbitMQ
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            System.err.println("❌ Error processing event: " + e.getMessage());
            e.printStackTrace();
            // Requeue the message for retry
            channel.basicNack(deliveryTag, false, true);
        }
    }
}