package com.example.CoreBack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SystemConfig {
    @Id
    private Long id = 1L;

    private int queueThreshold;
    private int latencyThreshold;
    private int errorThreshold;
    private String notificationEmail;
}