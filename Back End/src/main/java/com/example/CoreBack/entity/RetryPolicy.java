package com.example.CoreBack.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RetryPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int minDelay;
    private int maxDelay;
    private int maxTries;
    private double backoffMultiplier;
    private boolean enabled;
}