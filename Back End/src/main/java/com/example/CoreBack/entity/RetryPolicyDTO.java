package com.example.CoreBack.entity;

import lombok.Data;

@Data
public class RetryPolicyDTO {
    private Long id;
    private String name;
    private int minDelay;
    private int maxDelay;
    private int maxTries;
    private double backoffMultiplier;
    private boolean enabled;
}