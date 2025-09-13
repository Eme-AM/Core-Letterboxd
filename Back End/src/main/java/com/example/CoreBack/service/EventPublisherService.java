package com.example.CoreBack.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.example.CoreBack.config.RabbitConfig;

@Service
public class EventPublisherService {

    private final AmqpTemplate rabbitTemplate;

    public EventPublisherService(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Object message, String routingKey) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, message);
        System.out.println(" Evento enviado con routingKey = " + routingKey);
    }
}

