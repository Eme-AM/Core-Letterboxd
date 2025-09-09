package com.example.CoreBack.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.example.CoreBack.config.RabbitConfig;

@Service
public class RabbitEventPublisher implements EventPublisherTest {

    private final AmqpTemplate rabbitTemplate;

    public RabbitEventPublisher(AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Object message, String routingKey) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, routingKey, message);
        System.out.println("Evento enviado con routingKey = " + routingKey);
    }
}
