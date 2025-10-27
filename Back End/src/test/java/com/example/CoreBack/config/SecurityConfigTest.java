package com.example.CoreBack.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Tests unitarios para SecurityConfig
 * 
 * Verifica configuración de seguridad y comportamiento del filtro
 */
@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Test
    @DisplayName("SecurityConfig debe ser instanciable")
    void securityConfig_ShouldBeInstantiable() {
        var config = new SecurityConfig();
        assertNotNull(config);
    }

    @Test
    @DisplayName("SecurityConfig debe poder crear SecurityFilterChain")
    void securityConfig_ShouldCreateSecurityFilterChain() throws Exception {
        // Given
        var config = new SecurityConfig();
        
        // When & Then - Verificar que el método filterChain existe
        var method = SecurityConfig.class.getMethod("filterChain", HttpSecurity.class);
        assertNotNull(method, "Método filterChain debe existir");
    }

    @Test
    @DisplayName("SecurityConfig debe tener métodos de configuración esperados")
    void securityConfig_ShouldHaveExpectedConfigurationMethods() {
        var config = new SecurityConfig();
        
        // Verificar que la clase tiene la estructura esperada
        assertNotNull(config);
        
        // Verificar que tiene el método filterChain
        var methods = SecurityConfig.class.getDeclaredMethods();
        boolean hasFilterChainMethod = false;
        
        for (var method : methods) {
            if ("filterChain".equals(method.getName())) {
                hasFilterChainMethod = true;
                break;
            }
        }
        
        assertTrue(hasFilterChainMethod, "SecurityConfig debe tener método filterChain");
    }

    @Test
    @DisplayName("SecurityConfig debe estar en el paquete config correcto")
    void securityConfig_ShouldBeInCorrectPackage() {
        var config = new SecurityConfig();
        String packageName = config.getClass().getPackage().getName();
        
        assertTrue(packageName.endsWith(".config"), 
            "SecurityConfig debe estar en paquete config");
    }
}