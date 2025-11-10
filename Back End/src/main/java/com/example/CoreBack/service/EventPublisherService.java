package com.example.CoreBack.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.CoreBack.config.RabbitConfig;
import com.example.CoreBack.entity.StoredEvent;
import com.example.CoreBack.repository.EventRepository;

@Service
public class EventPublisherService {

    private final AmqpTemplate rabbitTemplate;
    private final EventRepository eventRepository;

    public EventPublisherService(AmqpTemplate rabbitTemplate, EventRepository eventRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.eventRepository = eventRepository;
    }

    // Intenta enviar un evento puntual (idempotente a nivel app)
    public void trySend(StoredEvent ev) {
        if (ev == null || !"PENDING".equalsIgnoreCase(ev.getStatus())) return;

        try {
            // Enviar con messageId para idempotencia downstream
            rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                ev.getRoutingKey(),
                ev.getPayload(), // si guardaste JSON en payload
                msg -> {
                    msg.getMessageProperties().setMessageId(ev.getMessageId());
                    msg.getMessageProperties().setContentType(
                        ev.getContentType() != null ? ev.getContentType() : "application/json"
                    );
                    return msg;
                }
            );

            ev.setStatus("DELIVERED");
            ev.setDeliveredAt(LocalDateTime.now());
            ev.setError(null);
            eventRepository.save(ev);

            System.out.println("Evento OUTBOX entregado routingKey=" + ev.getRoutingKey());

        } catch (org.springframework.amqp.AmqpConnectException ce) {
            // Broker caído -> planificar reintento
            scheduleRetry(ev, "Broker down: " + ce.getMessage());

        } catch (Exception ex) {
            // Otros errores (exchange/routingKey/serialización, etc.)
            scheduleRetry(ev, ex.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private void scheduleRetry(StoredEvent ev, String error) {
        int attempts = ev.getAttempts() == null ? 0 : ev.getAttempts();
        attempts++;

        // Backoff exponencial con tope de 5 minutos
        long seconds = (long) Math.min(300, Math.pow(2, Math.max(0, attempts - 1))); // 1,2,4,8,16,32,... <= 300
        ev.setAttempts(attempts);
        ev.setNextAttemptAt(LocalDateTime.now().plusSeconds(seconds));
        ev.setStatus("PENDING");
        ev.setError(error);
        eventRepository.save(ev);
    }

    // ======= Job que reenvía pendientes =========
    @Scheduled(fixedDelay = 5000) // cada 5s
    public void resendPending() {
        List<StoredEvent> pendings =
            eventRepository.findTop100ByStatusInAndNextAttemptAtBeforeOrderByNextAttemptAtAsc(
                List.of("PENDING"), LocalDateTime.now()
            );

        for (StoredEvent ev : pendings) {
            trySend(ev);
        }
    }
}
