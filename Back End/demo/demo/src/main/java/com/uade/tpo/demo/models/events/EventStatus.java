package com.uade.tpo.demo.models.events;

public enum EventStatus {
    PENDING,
    PROCESSING,
    PROCESSED,
    FAILED,
    RETRYING,
    DEAD_LETTER
}
