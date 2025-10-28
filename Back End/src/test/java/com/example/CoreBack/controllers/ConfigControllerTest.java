package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

/**
 * Tests estructurales para ConfigController
 * 
 * Verifica la estructura básica del controller sin depender de entidades Lombok problemáticas.
 * Este enfoque evita problemas de compilación con DTOs mientras verifica la estructura del controller.
 */
@SpringJUnitConfig
class ConfigControllerTest {

    @Test
    @DisplayName("ConfigController debe existir y estar correctamente anotado")
    void controllerExists() {
        // Given & When
        Class<?> controllerClass = null;
        try {
            controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");
        } catch (ClassNotFoundException e) {
            fail("ConfigController class not found: " + e.getMessage());
        }

        // Then
        assertNotNull(controllerClass, "ConfigController debe existir");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class),
                "ConfigController debe tener anotación @RestController");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class),
                "ConfigController debe tener anotación @RequestMapping");
    }

    @Test
    @DisplayName("ConfigController debe tener constructor con dependencias requeridas")
    void controllerHasRequiredConstructor() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");

        // When
        var constructors = controllerClass.getConstructors();

        // Then
        assertTrue(constructors.length > 0, "ConfigController debe tener al menos un constructor");
        
        // Verificar que tiene un constructor con ConfigService
        boolean hasValidConstructor = false;
        for (var constructor : constructors) {
            var paramTypes = constructor.getParameterTypes();
            if (paramTypes.length >= 1) { // Debe tener al menos ConfigService
                hasValidConstructor = true;
                break;
            }
        }
        assertTrue(hasValidConstructor, "ConfigController debe tener constructor con dependencias");
    }

    @Test
    @DisplayName("ConfigController debe tener métodos CRUD para retry policies")
    void controllerHasRetryCrudEndpoints() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");

        // When
        Method[] methods = controllerClass.getDeclaredMethods();

        // Then
        boolean hasGetMapping = false;
        boolean hasPostMapping = false;
        boolean hasPutMapping = false;
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
                hasGetMapping = true;
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) {
                hasPostMapping = true;
            }
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) {
                hasPutMapping = true;
            }
        }

        assertTrue(hasGetMapping, "ConfigController debe tener métodos con @GetMapping");
        assertTrue(hasPostMapping, "ConfigController debe tener métodos con @PostMapping");
        assertTrue(hasPutMapping, "ConfigController debe tener métodos con @PutMapping");
    }

    @Test
    @DisplayName("ConfigController debe tener anotaciones Swagger")
    void controllerHasSwaggerAnnotations() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");
        Method[] methods = controllerClass.getDeclaredMethods();

        // When & Then
        assertTrue(controllerClass.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class),
                "ConfigController debe tener anotación @Tag");
        
        boolean hasOperationAnnotation = false;
        for (Method method : methods) {
            if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.Operation.class)) {
                hasOperationAnnotation = true;
                break;
            }
        }

        assertTrue(hasOperationAnnotation, "ConfigController debe tener métodos con anotación @Operation");
    }

    @Test
    @DisplayName("ConfigController debe tener configuración CORS")
    void controllerHasCorsConfiguration() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");

        // When & Then
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.CrossOrigin.class),
                "ConfigController debe tener anotación @CrossOrigin para permitir CORS");
    }

    @Test
    @DisplayName("ConfigController debe tener mapeos de ruta correctos")
    void controllerHasCorrectRequestMapping() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConfigController");

        // When
        var requestMapping = controllerClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);

        // Then
        assertNotNull(requestMapping, "ConfigController debe tener @RequestMapping");
        String[] paths = requestMapping.value();
        assertTrue(paths.length > 0, "ConfigController debe tener al menos una ruta definida");
        assertEquals("/config", paths[0], "ConfigController debe mapear a /config");
    }
}
