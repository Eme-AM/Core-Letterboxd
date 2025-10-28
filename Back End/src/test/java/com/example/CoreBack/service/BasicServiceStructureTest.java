package com.example.CoreBack.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios básicos usando reflexión - sin Spring Context
 * 
 * Verifica existencia de servicios y métodos sin importar entidades Lombok
 */
@DisplayName("Basic Service Structure Tests")
public class BasicServiceStructureTest {

    @Test
    @DisplayName("RetryPolicyService - debe existir y tener métodos básicos")
    void retryPolicyService_ShouldExistWithBasicMethods() throws Exception {
        Class<?> serviceClass = Class.forName("com.example.CoreBack.service.RetryPolicyService");
        assertNotNull(serviceClass, "RetryPolicyService debe existir");

        // Verificar que tiene anotación @Service
        assertTrue(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class),
            "RetryPolicyService debe estar anotado con @Service");

        // Verificar métodos principales
        Method findAllMethod = serviceClass.getMethod("findAll");
        Method findByIdMethod = serviceClass.getMethod("findById", Long.class);
        Method deleteMethod = serviceClass.getMethod("delete", Long.class);
        
        assertNotNull(findAllMethod, "Debe tener método findAll");
        assertNotNull(findByIdMethod, "Debe tener método findById");
        assertNotNull(deleteMethod, "Debe tener método delete");
    }

    @Test
    @DisplayName("SystemConfigService - debe existir y tener métodos básicos")
    void systemConfigService_ShouldExistWithBasicMethods() throws Exception {
        Class<?> serviceClass = Class.forName("com.example.CoreBack.service.SystemConfigService");
        assertNotNull(serviceClass, "SystemConfigService debe existir");

        // Verificar que tiene anotación @Service
        assertTrue(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class),
            "SystemConfigService debe estar anotado con @Service");

        // Verificar método principal
        Method getConfigMethod = serviceClass.getMethod("getConfig");
        assertNotNull(getConfigMethod, "Debe tener método getConfig");
    }

    @Test
    @DisplayName("ModulePolicyService - debe existir y tener métodos básicos")
    void modulePolicyService_ShouldExistWithBasicMethods() throws Exception {
        Class<?> serviceClass = Class.forName("com.example.CoreBack.service.ModulePolicyService");
        assertNotNull(serviceClass, "ModulePolicyService debe existir");

        // Verificar que tiene anotación @Service
        assertTrue(serviceClass.isAnnotationPresent(org.springframework.stereotype.Service.class),
            "ModulePolicyService debe estar anotado con @Service");

        // Verificar métodos principales
        Method findAllMethod = serviceClass.getMethod("findAll");
        Method deleteMethod = serviceClass.getMethod("delete", Long.class);
        
        assertNotNull(findAllMethod, "Debe tener método findAll");
        assertNotNull(deleteMethod, "Debe tener método delete");
    }

    @Test
    @DisplayName("Repositorios - deben existir como interfaces JPA")
    void repositories_ShouldExistAsJpaInterfaces() throws Exception {
        String[] repositories = {
            "com.example.CoreBack.repository.RetryPolicyRepository",
            "com.example.CoreBack.repository.SystemConfigRepository", 
            "com.example.CoreBack.repository.ModulePolicyRepository"
        };

        for (String repoName : repositories) {
            Class<?> repoClass = Class.forName(repoName);
            assertTrue(repoClass.isInterface(), repoName + " debe ser una interfaz");
            
            // Verificar que extiende JpaRepository (indirectamente)
            assertTrue(repoClass.getInterfaces().length > 0 || 
                       repoClass.getSuperclass() != null,
                       repoName + " debe extender de alguna interfaz base");
        }
    }

    @Test
    @DisplayName("Todos los servicios - deben tener constructor público")
    void allServices_ShouldHavePublicConstructor() throws Exception {
        String[] services = {
            "com.example.CoreBack.service.RetryPolicyService",
            "com.example.CoreBack.service.SystemConfigService",
            "com.example.CoreBack.service.ModulePolicyService"
        };

        for (String serviceName : services) {
            Class<?> serviceClass = Class.forName(serviceName);
            
            // Verificar que tiene al menos un constructor público
            boolean hasPublicConstructor = false;
            var constructors = serviceClass.getConstructors();
            
            for (var constructor : constructors) {
                if (java.lang.reflect.Modifier.isPublic(constructor.getModifiers())) {
                    hasPublicConstructor = true;
                    break;
                }
            }
            
            assertTrue(hasPublicConstructor, serviceName + " debe tener constructor público");
        }
    }
}