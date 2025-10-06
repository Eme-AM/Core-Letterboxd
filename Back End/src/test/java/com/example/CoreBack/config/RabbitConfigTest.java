package com.example.CoreBack.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para RabbitConfig (sin Spring Context)
 * 
 * Verifica la existencia y configuración básica de RabbitConfig
 */
class RabbitConfigTest {

    @Test
    @DisplayName("Constante EXCHANGE debe existir y tener valor")
    void exchange_ShouldExist() {
        assertEquals("letterboxd_exchange", RabbitConfig.EXCHANGE);
    }

    @Test
    @DisplayName("RabbitConfig debe poder instanciarse")
    void rabbitConfig_ShouldBeInstantiable() {
        RabbitConfig config = new RabbitConfig();
        assertNotNull(config);
    }

    @Test
    @DisplayName("RabbitConfig debe ser una clase de configuración")
    void rabbitConfig_ShouldBeConfigurationClass() {
        assertTrue(RabbitConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }
}