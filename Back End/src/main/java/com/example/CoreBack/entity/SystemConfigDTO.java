package com.example.CoreBack.entity;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private int queueThreshold;
    private int latencyThreshold;
    private int errorThreshold;
    private String notificationEmail;
}