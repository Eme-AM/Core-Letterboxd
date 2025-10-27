package com.example.CoreBack.service;

import com.example.CoreBack.entity.RetryPolicy;
import com.example.CoreBack.repository.RetryPolicyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios completos para RetryPolicyService.
 * 
 * Cubre todos los métodos CRUD del servicio con casos exitosos y de error.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RetryPolicyService Tests")
class RetryPolicyServiceTest {

    @Mock
    private RetryPolicyRepository repository;

    @InjectMocks
    private RetryPolicyService retryPolicyService;

    private RetryPolicy testRetryPolicy;
    private RetryPolicy anotherRetryPolicy;

    @BeforeEach
    void setUp() {
        testRetryPolicy = new RetryPolicy();
        testRetryPolicy.setId(1L);
        testRetryPolicy.setName("Test Policy");
        testRetryPolicy.setMinDelay(1000);
        testRetryPolicy.setMaxDelay(5000);
        testRetryPolicy.setMaxTries(3);
        testRetryPolicy.setBackoffMultiplier(2.0);
        testRetryPolicy.setEnabled(true);

        anotherRetryPolicy = new RetryPolicy();
        anotherRetryPolicy.setId(2L);
        anotherRetryPolicy.setName("Another Policy");
        anotherRetryPolicy.setMinDelay(500);
        anotherRetryPolicy.setMaxDelay(3000);
        anotherRetryPolicy.setMaxTries(5);
        anotherRetryPolicy.setBackoffMultiplier(1.5);
        anotherRetryPolicy.setEnabled(false);
    }

    @Test
    @DisplayName("findAll - debería retornar lista de todas las políticas")
    void findAll_ShouldReturnAllPolicies() {
        // Given
        List<RetryPolicy> expectedPolicies = Arrays.asList(testRetryPolicy, anotherRetryPolicy);
        when(repository.findAll()).thenReturn(expectedPolicies);

        // When
        List<RetryPolicy> result = retryPolicyService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testRetryPolicy, result.get(0));
        assertEquals(anotherRetryPolicy, result.get(1));
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - debería retornar lista vacía cuando no hay políticas")
    void findAll_ShouldReturnEmptyListWhenNoPolicies() {
        // Given
        when(repository.findAll()).thenReturn(Arrays.asList());

        // When
        List<RetryPolicy> result = retryPolicyService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById - debería retornar política cuando existe")
    void findById_ShouldReturnPolicyWhenExists() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testRetryPolicy));

        // When
        RetryPolicy result = retryPolicyService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testRetryPolicy.getId(), result.getId());
        assertEquals(testRetryPolicy.getName(), result.getName());
        assertEquals(testRetryPolicy.getMinDelay(), result.getMinDelay());
        assertEquals(testRetryPolicy.getMaxDelay(), result.getMaxDelay());
        assertEquals(testRetryPolicy.getMaxTries(), result.getMaxTries());
        assertEquals(testRetryPolicy.getBackoffMultiplier(), result.getBackoffMultiplier());
        assertEquals(testRetryPolicy.isEnabled(), result.isEnabled());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - debería retornar null cuando no existe")
    void findById_ShouldReturnNullWhenNotExists() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When
        RetryPolicy result = retryPolicyService.findById(99L);

        // Then
        assertNull(result);
        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("save - debería guardar y retornar nueva política")
    void save_ShouldSaveAndReturnNewPolicy() {
        // Given
        RetryPolicy newPolicy = new RetryPolicy();
        newPolicy.setName("New Policy");
        newPolicy.setMinDelay(2000);
        newPolicy.setMaxDelay(8000);
        newPolicy.setMaxTries(4);
        newPolicy.setBackoffMultiplier(3.0);
        newPolicy.setEnabled(true);

        RetryPolicy savedPolicy = new RetryPolicy();
        savedPolicy.setId(3L);
        savedPolicy.setName("New Policy");
        savedPolicy.setMinDelay(2000);
        savedPolicy.setMaxDelay(8000);
        savedPolicy.setMaxTries(4);
        savedPolicy.setBackoffMultiplier(3.0);
        savedPolicy.setEnabled(true);

        when(repository.save(newPolicy)).thenReturn(savedPolicy);

        // When
        RetryPolicy result = retryPolicyService.save(newPolicy);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Policy", result.getName());
        assertEquals(2000, result.getMinDelay());
        assertEquals(8000, result.getMaxDelay());
        assertEquals(4, result.getMaxTries());
        assertEquals(3.0, result.getBackoffMultiplier());
        assertTrue(result.isEnabled());
        verify(repository, times(1)).save(newPolicy);
    }

    @Test
    @DisplayName("update - debería actualizar política existente")
    void update_ShouldUpdateExistingPolicy() {
        // Given
        RetryPolicy updateData = new RetryPolicy();
        updateData.setName("Updated Policy");
        updateData.setMinDelay(1500);
        updateData.setMaxDelay(6000);
        updateData.setMaxTries(6);
        updateData.setBackoffMultiplier(2.5);
        updateData.setEnabled(false);

        when(repository.findById(1L)).thenReturn(Optional.of(testRetryPolicy));
        when(repository.save(any(RetryPolicy.class))).thenReturn(testRetryPolicy);

        // When
        RetryPolicy result = retryPolicyService.update(1L, updateData);

        // Then
        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(testRetryPolicy);
        
        // Verificar que los valores se actualizaron en el objeto
        assertEquals("Updated Policy", testRetryPolicy.getName());
        assertEquals(1500, testRetryPolicy.getMinDelay());
        assertEquals(6000, testRetryPolicy.getMaxDelay());
        assertEquals(6, testRetryPolicy.getMaxTries());
        assertEquals(2.5, testRetryPolicy.getBackoffMultiplier());
        assertFalse(testRetryPolicy.isEnabled());
    }

    @Test
    @DisplayName("update - debería lanzar excepción cuando política no existe")
    void update_ShouldThrowExceptionWhenPolicyNotExists() {
        // Given
        RetryPolicy updateData = new RetryPolicy();
        updateData.setName("Updated Policy");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            retryPolicyService.update(99L, updateData);
        });

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(RetryPolicy.class));
    }

    @Test
    @DisplayName("delete - debería eliminar política por ID")
    void delete_ShouldDeletePolicyById() {
        // Given
        doNothing().when(repository).deleteById(1L);

        // When
        retryPolicyService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("save - debería manejar política con valores mínimos")
    void save_ShouldHandlePolicyWithMinimalValues() {
        // Given
        RetryPolicy minimalPolicy = new RetryPolicy();
        minimalPolicy.setName("Minimal");
        minimalPolicy.setMinDelay(0);
        minimalPolicy.setMaxDelay(1000);
        minimalPolicy.setMaxTries(1);
        minimalPolicy.setBackoffMultiplier(1.0);
        minimalPolicy.setEnabled(false);

        RetryPolicy savedPolicy = new RetryPolicy();
        savedPolicy.setId(4L);
        savedPolicy.setName("Minimal");
        savedPolicy.setMinDelay(0);
        savedPolicy.setMaxDelay(1000);
        savedPolicy.setMaxTries(1);
        savedPolicy.setBackoffMultiplier(1.0);
        savedPolicy.setEnabled(false);

        when(repository.save(minimalPolicy)).thenReturn(savedPolicy);

        // When
        RetryPolicy result = retryPolicyService.save(minimalPolicy);

        // Then
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Minimal", result.getName());
        assertEquals(0, result.getMinDelay());
        assertEquals(1000, result.getMaxDelay());
        assertEquals(1, result.getMaxTries());
        assertEquals(1.0, result.getBackoffMultiplier());
        assertFalse(result.isEnabled());
        verify(repository, times(1)).save(minimalPolicy);
    }

    @Test
    @DisplayName("update - debería actualizar todos los campos correctamente")
    void update_ShouldUpdateAllFieldsCorrectly() {
        // Given
        RetryPolicy originalPolicy = new RetryPolicy();
        originalPolicy.setId(5L);
        originalPolicy.setName("Original");
        originalPolicy.setMinDelay(100);
        originalPolicy.setMaxDelay(1000);
        originalPolicy.setMaxTries(2);
        originalPolicy.setBackoffMultiplier(1.2);
        originalPolicy.setEnabled(true);

        RetryPolicy updateData = new RetryPolicy();
        updateData.setName("Completely Updated");
        updateData.setMinDelay(5000);
        updateData.setMaxDelay(15000);
        updateData.setMaxTries(10);
        updateData.setBackoffMultiplier(4.0);
        updateData.setEnabled(false);

        when(repository.findById(5L)).thenReturn(Optional.of(originalPolicy));
        when(repository.save(originalPolicy)).thenReturn(originalPolicy);

        // When
        RetryPolicy result = retryPolicyService.update(5L, updateData);

        // Then
        assertNotNull(result);
        assertEquals("Completely Updated", originalPolicy.getName());
        assertEquals(5000, originalPolicy.getMinDelay());
        assertEquals(15000, originalPolicy.getMaxDelay());
        assertEquals(10, originalPolicy.getMaxTries());
        assertEquals(4.0, originalPolicy.getBackoffMultiplier());
        assertFalse(originalPolicy.isEnabled());
        
        verify(repository, times(1)).findById(5L);
        verify(repository, times(1)).save(originalPolicy);
    }
}