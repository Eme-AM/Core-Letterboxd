package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

/**
 * Tests estructurales para ConsumerController
 * 
 * Verifica la estructura básica del controller sin depender de entidades Lombok problemáticas.
 */
@SpringJUnitConfig
class ConsumerControllerTest {

    @Test
    @DisplayName("ConsumerController debe existir y estar correctamente anotado")
    void controllerExists() {
        // Given & When
        Class<?> controllerClass = null;
        try {
            controllerClass = Class.forName("com.example.CoreBack.controllers.ConsumerController");
        } catch (ClassNotFoundException e) {
            fail("ConsumerController class not found: " + e.getMessage());
        }

        // Then
        assertNotNull(controllerClass, "ConsumerController debe existir");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class),
                "ConsumerController debe tener anotación @RestController");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class),
                "ConsumerController debe tener anotación @RequestMapping");
    }

    @Test
    @DisplayName("ConsumerController debe tener constructor público")
    void consumerController_ShouldHavePublicConstructor() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConsumerController");

        // When
        var constructors = controllerClass.getConstructors();

        // Then
        assertTrue(constructors.length > 0, "ConsumerController debe tener al menos un constructor");
        
        // Verificar que tiene un constructor público (el constructor por defecto está bien)
        boolean hasValidConstructor = false;
        for (var constructor : constructors) {
            if (constructor.getParameterCount() == 0) { // Constructor por defecto  
                hasValidConstructor = true;
                break;
            }
        }
        assertTrue(hasValidConstructor, "ConsumerController debe tener constructor público");
    }

    @Test
    @DisplayName("ConsumerController debe tener endpoints para gestión de consumidores")
    void controllerHasConsumerEndpoints() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConsumerController");

        // When
        Method[] methods = controllerClass.getDeclaredMethods();

        // Then
        boolean hasMapping = false;
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class) ||
                method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class) ||
                method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class) ||
                method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) {
                hasMapping = true;
                break;
            }
        }

        assertTrue(hasMapping, "ConsumerController debe tener al menos un endpoint mapeado");
    }

    @Test
    @DisplayName("ConsumerController debe tener anotaciones Swagger")
    void controllerHasSwaggerAnnotations() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConsumerController");

        // When & Then
        assertTrue(controllerClass.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class),
                "ConsumerController debe tener anotación @Tag para documentación");
    }

    @Test
    @DisplayName("ConsumerController debe tener mapeo de ruta correcto")
    void controllerHasCorrectRequestMapping() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.ConsumerController");

        // When
        var requestMapping = controllerClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);

        // Then
        assertNotNull(requestMapping, "ConsumerController debe tener @RequestMapping");
        String[] paths = requestMapping.value();
        assertTrue(paths.length > 0, "ConsumerController debe tener al menos una ruta definida");
        assertEquals("/consumers", paths[0], "ConsumerController debe mapear a /consumers");
    }
}
