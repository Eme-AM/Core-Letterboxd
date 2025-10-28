package com.example.CoreBack.service;

import com.example.CoreBack.entity.RetryPolicy;
import com.example.CoreBack.entity.RetryPolicyDTO;
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
 * Tests unitarios completos para ConfigService.
 * 
 * Cubre todos los métodos CRUD del servicio
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigService Tests")
class ConfigServiceTest {

    @Mock
    private RetryPolicyRepository retryPolicyRepository;

    @InjectMocks
    private ConfigService configService;

    private RetryPolicyDTO testRetryPolicyDTO;
    private RetryPolicy testRetryPolicy;

    @BeforeEach
    void setUp() {
        testRetryPolicyDTO = new RetryPolicyDTO();
        testRetryPolicyDTO.setId(1L);
        testRetryPolicyDTO.setName("Test Policy");
        testRetryPolicyDTO.setMinDelay(1000);
        testRetryPolicyDTO.setMaxDelay(5000);
        testRetryPolicyDTO.setMaxTries(3);
        testRetryPolicyDTO.setBackoffMultiplier(2.0);
        testRetryPolicyDTO.setEnabled(true);

        testRetryPolicy = new RetryPolicy();
        testRetryPolicy.setId(1L);
        testRetryPolicy.setName("Test Policy");
        testRetryPolicy.setMinDelay(1000);
        testRetryPolicy.setMaxDelay(5000);
        testRetryPolicy.setMaxTries(3);
        testRetryPolicy.setBackoffMultiplier(2.0);
        testRetryPolicy.setEnabled(true);
    }

    @Test
    @DisplayName("Crear RetryPolicy - debería crear y retornar DTO con ID")
    void createRetryPolicy_ShouldCreateAndReturnDTOWithId() {
        // Given
        RetryPolicyDTO inputDTO = new RetryPolicyDTO();
        inputDTO.setName("New Policy");
        inputDTO.setMinDelay(500);
        inputDTO.setMaxDelay(2000);
        inputDTO.setMaxTries(5);
        inputDTO.setBackoffMultiplier(1.5);
        inputDTO.setEnabled(false);

        RetryPolicy savedPolicy = new RetryPolicy();
        savedPolicy.setId(2L);
        savedPolicy.setName("New Policy");

        when(retryPolicyRepository.save(any(RetryPolicy.class))).thenReturn(savedPolicy);

        // When
        RetryPolicyDTO result = configService.createRetryPolicy(inputDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("New Policy", result.getName());
        assertEquals(500, result.getMinDelay());
        assertEquals(2000, result.getMaxDelay());
        assertEquals(5, result.getMaxTries());
        assertEquals(1.5, result.getBackoffMultiplier());
        assertFalse(result.isEnabled());

        verify(retryPolicyRepository, times(1)).save(any(RetryPolicy.class));
    }

    @Test
    @DisplayName("Listar RetryPolicies - debería retornar lista de DTOs")
    void listRetryPolicies_ShouldReturnListOfDTOs() {
        // Given
        RetryPolicy policy1 = new RetryPolicy();
        policy1.setId(1L);
        policy1.setName("Policy 1");
        policy1.setMinDelay(1000);
        policy1.setMaxDelay(3000);
        policy1.setMaxTries(2);
        policy1.setBackoffMultiplier(1.5);
        policy1.setEnabled(true);

        RetryPolicy policy2 = new RetryPolicy();
        policy2.setId(2L);
        policy2.setName("Policy 2");
        policy2.setMinDelay(2000);
        policy2.setMaxDelay(6000);
        policy2.setMaxTries(4);
        policy2.setBackoffMultiplier(2.0);
        policy2.setEnabled(false);

        when(retryPolicyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        // When
        List<RetryPolicyDTO> result = configService.listRetryPolicies();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        RetryPolicyDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Policy 1", dto1.getName());
        assertEquals(1000, dto1.getMinDelay());
        assertTrue(dto1.isEnabled());

        RetryPolicyDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Policy 2", dto2.getName());
        assertEquals(2000, dto2.getMinDelay());
        assertFalse(dto2.isEnabled());

        verify(retryPolicyRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener RetryPolicy por ID - debería retornar DTO cuando existe")
    void getRetryPolicy_ShouldReturnDTOWhenExists() {
        // Given
        when(retryPolicyRepository.findById(1L)).thenReturn(Optional.of(testRetryPolicy));

        // When
        RetryPolicyDTO result = configService.getRetryPolicy(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Policy", result.getName());
        assertEquals(1000, result.getMinDelay());
        assertEquals(5000, result.getMaxDelay());
        assertEquals(3, result.getMaxTries());
        assertEquals(2.0, result.getBackoffMultiplier());
        assertTrue(result.isEnabled());

        verify(retryPolicyRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtener RetryPolicy por ID - debería retornar null cuando no existe")
    void getRetryPolicy_ShouldReturnNullWhenNotExists() {
        // Given
        when(retryPolicyRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        RetryPolicyDTO result = configService.getRetryPolicy(99L);

        // Then
        assertNull(result);
        verify(retryPolicyRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Actualizar RetryPolicy - debería actualizar y retornar DTO")
    void updateRetryPolicy_ShouldUpdateAndReturnDTO() {
        // Given
        RetryPolicyDTO updateDTO = new RetryPolicyDTO();
        updateDTO.setName("Updated Policy");
        updateDTO.setMinDelay(2000);
        updateDTO.setMaxDelay(8000);
        updateDTO.setMaxTries(5);
        updateDTO.setBackoffMultiplier(3.0);
        updateDTO.setEnabled(false);

        when(retryPolicyRepository.findById(1L)).thenReturn(Optional.of(testRetryPolicy));
        when(retryPolicyRepository.save(any(RetryPolicy.class))).thenReturn(testRetryPolicy);

        // When
        RetryPolicyDTO result = configService.updateRetryPolicy(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Policy", result.getName());
        assertEquals(2000, result.getMinDelay());
        assertEquals(8000, result.getMaxDelay());
        assertEquals(5, result.getMaxTries());
        assertEquals(3.0, result.getBackoffMultiplier());
        assertFalse(result.isEnabled());

        verify(retryPolicyRepository, times(1)).findById(1L);
        verify(retryPolicyRepository, times(1)).save(any(RetryPolicy.class));
    }

    @Test
    @DisplayName("Actualizar RetryPolicy - debería lanzar excepción cuando no existe")
    void updateRetryPolicy_ShouldThrowExceptionWhenNotExists() {
        // Given
        RetryPolicyDTO updateDTO = new RetryPolicyDTO();
        updateDTO.setName("Updated Policy");

        when(retryPolicyRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            configService.updateRetryPolicy(99L, updateDTO);
        });

        verify(retryPolicyRepository, times(1)).findById(99L);
        verify(retryPolicyRepository, never()).save(any(RetryPolicy.class));
    }

    @Test
    @DisplayName("Eliminar RetryPolicy - debería llamar al repositorio")
    void deleteRetryPolicy_ShouldCallRepository() {
        // Given
        doNothing().when(retryPolicyRepository).deleteById(1L);

        // When
        configService.deleteRetryPolicy(1L);

        // Then
        verify(retryPolicyRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Constructor - debería inyectar dependencias correctamente")
    void constructor_ShouldInjectDependenciesCorrectly() {
        // Given & When
        ConfigService service = new ConfigService(retryPolicyRepository);

        // Then
        assertNotNull(service);
    }
}