package com.uade.tpo.demo.service;

import com.uade.tpo.demo.models.dto.HealthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthService implements HealthIndicator {
    
    private final DataSource dataSource;
    private final RabbitAdmin rabbitAdmin;
    
    public HealthStatus getHealthStatus() {
        long timestamp = System.currentTimeMillis();
        
        HealthStatus.ConnectionHealth dbHealth = checkDatabaseHealth();
        HealthStatus.ConnectionHealth rabbitHealth = checkRabbitMQHealth();
        HealthStatus.QueueHealth queueHealth = checkQueueHealth();
        
        String overallStatus = determineOverallStatus(dbHealth, rabbitHealth, queueHealth);
        
        return HealthStatus.builder()
                .overallStatus(overallStatus)
                .timestamp(timestamp)
                .database(dbHealth)
                .rabbitMQ(rabbitHealth)
                .queues(queueHealth)
                .build();
    }
    
    @Override
    public Health health() {
        HealthStatus status = getHealthStatus();
        
        Health.Builder builder = "HEALTHY".equals(status.getOverallStatus()) 
                ? Health.up() : Health.down();
        
        return builder
                .withDetail("database", status.getDatabase())
                .withDetail("rabbitMQ", status.getRabbitMQ())
                .withDetail("queues", status.getQueues())
                .build();
    }
    
    private HealthStatus.ConnectionHealth checkDatabaseHealth() {
        long startTime = System.currentTimeMillis();
        try {
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5); // 5 second timeout
                long responseTime = System.currentTimeMillis() - startTime;
                
                return HealthStatus.ConnectionHealth.builder()
                        .status(isValid ? "HEALTHY" : "UNHEALTHY")
                        .message(isValid ? "Database connection is healthy" : "Database connection is invalid")
                        .responseTime(responseTime)
                        .build();
            }
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("Database health check failed", e);
            
            return HealthStatus.ConnectionHealth.builder()
                    .status("UNHEALTHY")
                    .message("Database connection failed: " + e.getMessage())
                    .responseTime(responseTime)
                    .build();
        }
    }
    
    private HealthStatus.ConnectionHealth checkRabbitMQHealth() {
        long startTime = System.currentTimeMillis();
        try {
            Properties properties = rabbitAdmin.getQueueProperties("letterboxd.incoming.events");
            long responseTime = System.currentTimeMillis() - startTime;
            
            boolean isHealthy = properties != null;
            
            return HealthStatus.ConnectionHealth.builder()
                    .status(isHealthy ? "HEALTHY" : "UNHEALTHY")
                    .message(isHealthy ? "RabbitMQ connection is healthy" : "RabbitMQ connection failed")
                    .responseTime(responseTime)
                    .build();
                    
        } catch (Exception e) {
            long responseTime = System.currentTimeMillis() - startTime;
            log.error("RabbitMQ health check failed", e);
            
            return HealthStatus.ConnectionHealth.builder()
                    .status("UNHEALTHY")
                    .message("RabbitMQ connection failed: " + e.getMessage())
                    .responseTime(responseTime)
                    .build();
        }
    }
    
    private HealthStatus.QueueHealth checkQueueHealth() {
        try {
            List<HealthStatus.QueueHealth.QueueInfo> queueInfos = new ArrayList<>();
            String[] queues = {
                "letterboxd.incoming.events",
                "letterboxd.user.events",
                "letterboxd.movie.events",
                "letterboxd.review.events",
                "letterboxd.social.events",
                "letterboxd.discovery.events",
                "letterboxd.analytics.events",
                "letterboxd.retry",
                "letterboxd.dlq"
            };
            
            for (String queueName : queues) {
                try {
                    Properties properties = rabbitAdmin.getQueueProperties(queueName);
                    if (properties != null) {
                        int messageCount = (Integer) properties.get("QUEUE_MESSAGE_COUNT");
                        int consumerCount = (Integer) properties.get("QUEUE_CONSUMER_COUNT");
                        
                        String status = determineQueueStatus(messageCount, consumerCount, queueName);
                        
                        queueInfos.add(HealthStatus.QueueHealth.QueueInfo.builder()
                                .queueName(queueName)
                                .messageCount(messageCount)
                                .consumerCount(consumerCount)
                                .status(status)
                                .build());
                    }
                } catch (Exception e) {
                    queueInfos.add(HealthStatus.QueueHealth.QueueInfo.builder()
                            .queueName(queueName)
                            .messageCount(-1)
                            .consumerCount(-1)
                            .status("ERROR")
                            .build());
                    log.warn("Failed to get info for queue: {}", queueName, e);
                }
            }
            
            String overallQueueStatus = queueInfos.stream()
                    .anyMatch(q -> "ERROR".equals(q.getStatus())) ? "UNHEALTHY" : "HEALTHY";
            
            return HealthStatus.QueueHealth.builder()
                    .status(overallQueueStatus)
                    .queueInfos(queueInfos)
                    .build();
                    
        } catch (Exception e) {
            log.error("Queue health check failed", e);
            return HealthStatus.QueueHealth.builder()
                    .status("UNHEALTHY")
                    .queueInfos(new ArrayList<>())
                    .build();
        }
    }
    
    private String determineQueueStatus(int messageCount, int consumerCount, String queueName) {
        // DLQ should ideally be empty
        if (queueName.contains("dlq") && messageCount > 0) {
            return "WARNING";
        }
        
        // Retry queue with messages is expected but should be monitored
        if (queueName.contains("retry") && messageCount > 10) {
            return "WARNING";
        }
        
        // Normal queues with too many messages might indicate processing issues
        if (messageCount > 100) {
            return "WARNING";
        }
        
        return "HEALTHY";
    }
    
    private String determineOverallStatus(HealthStatus.ConnectionHealth dbHealth, 
                                        HealthStatus.ConnectionHealth rabbitHealth, 
                                        HealthStatus.QueueHealth queueHealth) {
        if ("UNHEALTHY".equals(dbHealth.getStatus()) || "UNHEALTHY".equals(rabbitHealth.getStatus())) {
            return "UNHEALTHY";
        }
        
        if ("UNHEALTHY".equals(queueHealth.getStatus()) || 
            queueHealth.getQueueInfos().stream().anyMatch(q -> "WARNING".equals(q.getStatus()))) {
            return "DEGRADED";
        }
        
        return "HEALTHY";
    }
}
