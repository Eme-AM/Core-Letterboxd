package com.example.CoreBack.service;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.example.CoreBack.config.RabbitConfig;

@Service
public class EventConsumerService {

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receiveMessage(Map<String, Object> message) {
        System.out.println("📩 Evento recibido: " + message);
    }
}

