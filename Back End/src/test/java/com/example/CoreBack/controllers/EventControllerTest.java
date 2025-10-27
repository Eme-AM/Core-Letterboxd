package com.example.CoreBack.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests estructurales para EventController
 * 
 * Verifica:
 * - Estructura correcta del controller
 * - Anotaciones de Spring Web correctas
 * - Endpoints definidos correctamente
 * - Documentación Swagger presente
 * - Métodos públicos accesibles
 * - Inyección de dependencias correcta
 */
@DisplayName("EventController - Structural Tests")
class EventControllerTest {

    @Test
    @DisplayName("Controller debe tener la anotación @RestController")
    void eventControllerShouldHaveRestControllerAnnotation() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            assertTrue(controllerClass.isAnnotationPresent(RestController.class),
                    "EventController debe estar anotado con @RestController");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe tener mapping base '/events'")
    void eventControllerShouldHaveCorrectRequestMapping() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            assertTrue(controllerClass.isAnnotationPresent(RequestMapping.class),
                    "EventController debe tener @RequestMapping");
            
            RequestMapping mapping = controllerClass.getAnnotation(RequestMapping.class);
            String[] paths = mapping.value();
            assertTrue(paths.length > 0 && "/events".equals(paths[0]),
                    "EventController debe tener mapping '/events'");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe ser una clase pública no abstracta")
    void eventControllerShouldHaveCorrectModifiers() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            int modifiers = controllerClass.getModifiers();
            
            assertTrue(Modifier.isPublic(modifiers),
                    "EventController debe ser público");
            assertFalse(Modifier.isAbstract(modifiers),
                    "EventController no debe ser abstracto");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe tener anotación CrossOrigin")
    void eventControllerShouldHaveCrossOriginAnnotation() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            assertTrue(controllerClass.isAnnotationPresent(org.springframework.web.bind.annotation.CrossOrigin.class),
                    "EventController debe tener anotación @CrossOrigin");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe tener endpoint POST para recibir eventos")
    void eventControllerShouldHaveReceiveEventEndpoint() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            Method[] methods = controllerClass.getDeclaredMethods();
            
            boolean hasReceiveEventMethod = false;
            for (Method method : methods) {
                if (method.isAnnotationPresent(PostMapping.class) && 
                    Modifier.isPublic(method.getModifiers())) {
                    PostMapping postMapping = method.getAnnotation(PostMapping.class);
                    String[] paths = postMapping.value();
                    if (paths.length > 0 && "/receive".equals(paths[0])) {
                        hasReceiveEventMethod = true;
                        
                        // Verificar parámetros
                        Class<?>[] paramTypes = method.getParameterTypes();
                        assertTrue(paramTypes.length >= 1,
                                "Método receiveEvent debe tener al menos un parámetro");
                        
                        // Verificar que tiene anotación @RequestBody
                        assertTrue(method.getParameters()[0].isAnnotationPresent(RequestBody.class),
                                "Primer parámetro debe tener @RequestBody");
                        
                        break;
                    }
                }
            }
            
            assertTrue(hasReceiveEventMethod,
                    "EventController debe tener endpoint POST /receive para recibir eventos");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe tener endpoint GET para obtener todos los eventos")
    void eventControllerShouldHaveGetAllEventsEndpoint() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            Method[] methods = controllerClass.getDeclaredMethods();
            
            boolean hasGetAllEventsMethod = false;
            for (Method method : methods) {
                if (method.isAnnotationPresent(GetMapping.class) && 
                    Modifier.isPublic(method.getModifiers()) &&
                    "getAllEvents".equals(method.getName())) {
                    hasGetAllEventsMethod = true;
                    break;
                }
            }
            
            assertTrue(hasGetAllEventsMethod,
                    "EventController debe tener endpoint GET getAllEvents");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe tener campos de dependencias")
    void eventControllerShouldHaveDependencyFields() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            
            // Verificar que tiene campos de dependencias
            var fields = controllerClass.getDeclaredFields();
            boolean hasEventRepository = false;
            boolean hasEventService = false;
            
            for (var field : fields) {
                if ("eventRepository".equals(field.getName())) {
                    hasEventRepository = true;
                }
                if ("eventService".equals(field.getName())) {
                    hasEventService = true;
                }
            }
            
            assertTrue(hasEventRepository,
                    "EventController debe tener campo eventRepository");
            assertTrue(hasEventService,
                    "EventController debe tener campo eventService");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Métodos del controller deben tener documentación de operaciones")
    void eventControllerMethodsShouldHaveOperationDocumentation() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            Method[] methods = controllerClass.getDeclaredMethods();
            
            int publicMethodsWithMapping = 0;
            int methodsWithOperationDoc = 0;
            
            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers()) && 
                    (method.isAnnotationPresent(PostMapping.class) || 
                     method.isAnnotationPresent(GetMapping.class))) {
                    publicMethodsWithMapping++;
                    
                    if (method.isAnnotationPresent(Operation.class)) {
                        methodsWithOperationDoc++;
                    }
                }
            }
            
            assertTrue(publicMethodsWithMapping > 0,
                    "EventController debe tener métodos públicos con mappings");
            assertEquals(publicMethodsWithMapping, methodsWithOperationDoc,
                    "Todos los endpoints deben tener documentación @Operation");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Controller debe ser instanciable")
    void eventControllerShouldBeInstantiable() {
        try {
            Class<?> controllerClass = Class.forName("com.example.CoreBack.controllers.EventController");
            
            // Verificar que no es una clase abstracta o interface
            assertFalse(controllerClass.isInterface(),
                    "EventController no debe ser una interface");
            assertFalse(Modifier.isAbstract(controllerClass.getModifiers()),
                    "EventController no debe ser abstracto");
            
            // Verificar que tiene constructor accesible
            assertTrue(controllerClass.getConstructors().length > 0,
                    "EventController debe tener al menos un constructor");
        } catch (ClassNotFoundException e) {
            fail("EventController class not found: " + e.getMessage());
        }
    }
}