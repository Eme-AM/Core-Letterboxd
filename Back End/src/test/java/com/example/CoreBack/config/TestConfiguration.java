package com.example.CoreBack.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de testing que excluye Spring Security para evitar 
 * problemas de configuración en tests unitarios.
 */
@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class TestConfiguration {
}
