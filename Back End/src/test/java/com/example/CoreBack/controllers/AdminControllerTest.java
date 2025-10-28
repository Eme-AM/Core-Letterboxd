package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

/**
 * Tests estructurales para AdminController
 * 
 * Verifica la estructura básica del controller de administración.
 */
@SpringJUnitConfig
class AdminControllerTest {

    @Test
    @DisplayName("AdminController debe existir y estar correctamente anotado")
    void controllerExists() {
        // Given & When
        Class<?> controllerClass = null;
        try {
            controllerClass = Class.forName("com.example.CoreBack.controllers.AdminController");
        } catch (ClassNotFoundException e) {
            fail("AdminController class not found: " + e.getMessage());
        }

        // Then
        assertNotNull(controllerClass, "AdminController debe existir");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class),
                "AdminController debe tener anotación @RestController");
        assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class),
                "AdminController debe tener anotación @RequestMapping");
    }

    @Test
    @DisplayName("AdminController debe tener constructor público")
    void adminController_ShouldHavePublicConstructor() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.AdminController");

        // When
        var constructors = controllerClass.getConstructors();

        // Then
        assertTrue(constructors.length > 0, "AdminController debe tener al menos un constructor");
        
        // Verificar que tiene al menos un constructor público
        boolean hasPublicConstructor = false;
        for (var constructor : constructors) {
            if (java.lang.reflect.Modifier.isPublic(constructor.getModifiers())) {
                hasPublicConstructor = true;
                break;
            }
        }
        assertTrue(hasPublicConstructor, "AdminController debe tener constructor público");
    }

    @Test
    @DisplayName("AdminController debe tener endpoints de administración")
    void controllerHasAdminEndpoints() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.AdminController");

        // When
        Method[] methods = controllerClass.getDeclaredMethods();

        // Then
        boolean hasGetMapping = false;
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) {
                hasGetMapping = true;
                break;
            }
        }

        assertTrue(hasGetMapping, "AdminController debe tener al menos un endpoint GET para consultas");
    }

    @Test
    @DisplayName("AdminController debe tener anotaciones Swagger para documentación")
    void controllerHasSwaggerAnnotations() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.AdminController");
        Method[] methods = controllerClass.getDeclaredMethods();

        // When & Then
        assertTrue(controllerClass.isAnnotationPresent(io.swagger.v3.oas.annotations.tags.Tag.class),
                "AdminController debe tener anotación @Tag");
        
        boolean hasOperationAnnotation = false;
        boolean hasApiResponsesAnnotation = false;
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.Operation.class)) {
                hasOperationAnnotation = true;
            }
            if (method.isAnnotationPresent(io.swagger.v3.oas.annotations.responses.ApiResponses.class)) {
                hasApiResponsesAnnotation = true;
            }
        }

        assertTrue(hasOperationAnnotation, "AdminController debe tener métodos con @Operation");
        assertTrue(hasApiResponsesAnnotation, "AdminController debe tener métodos con @ApiResponses");
    }

    @Test
    @DisplayName("AdminController debe mapear a ruta /admin")
    void controllerHasCorrectRequestMapping() throws Exception {
        // Given
        Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.AdminController");

        // When
        var requestMapping = controllerClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);

        // Then
        assertNotNull(requestMapping, "AdminController debe tener @RequestMapping");
        String[] paths = requestMapping.value();
        assertTrue(paths.length > 0, "AdminController debe tener al menos una ruta definida");
        assertEquals("/admin", paths[0], "AdminController debe mapear a /admin");
    }
}
