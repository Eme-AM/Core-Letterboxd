package com.example.CoreBack.config;

import com.example.CoreBack.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para SecurityConfig (sin Spring Context)
 * 
 * Verifica que la clase de configuración existe y puede instanciarse
 */
class SecurityConfigTest {

    @Test
    @DisplayName("SecurityConfig debe poder instanciarse")
    void securityConfig_ShouldBeInstantiable() {
        SecurityConfig config = new SecurityConfig();
        assertNotNull(config);
    }

    @Test
    @DisplayName("SecurityConfig debe existir como clase de configuración")
    void securityConfig_ShouldExistAsConfigurationClass() {
        // Verificar que la clase existe y tiene la estructura esperada
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }
}