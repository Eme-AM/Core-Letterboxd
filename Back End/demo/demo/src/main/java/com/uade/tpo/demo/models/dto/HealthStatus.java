package com.uade.tpo.demo.models.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class HealthStatus {
    
    private String overallStatus; // HEALTHY, DEGRADED, UNHEALTHY
    private long timestamp;
    
    private ConnectionHealth database;
    private ConnectionHealth rabbitMQ;
    private QueueHealth queues;
    
    @Data
    @Builder
    public static class ConnectionHealth {
        private String status;
        private String message;
        private long responseTime;
    }
    
    @Data
    @Builder
    public static class QueueHealth {
        private String status;
        private List<QueueInfo> queueInfos;
        
        @Data
        @Builder
        public static class QueueInfo {
            private String queueName;
            private long messageCount;
            private long consumerCount;
            private String status;
        }
    }
}
