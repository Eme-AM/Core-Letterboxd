package com.example.CoreBack.service;

import com.example.CoreBack.entity.ModulePolicy;
import com.example.CoreBack.repository.ModulePolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ModulePolicyService
 * 
 * Tests coverage:
 * - findAll() method with various scenarios
 * - save() method with success and failure cases
 * - delete() method with different inputs
 * - Error handling scenarios
 */
@ExtendWith(MockitoExtension.class)
class ModulePolicyServiceTest {

    @Mock
    private ModulePolicyRepository repository;

    @InjectMocks
    private ModulePolicyService modulePolicyService;

    @Test
    @DisplayName("findAll should return all module policies")
    void findAll_ShouldReturnAllPolicies() {
        // Given
        List<ModulePolicy> policies = Arrays.asList(
            createModulePolicy("users", "User module policy"),
            createModulePolicy("events", "Event module policy"),
            createModulePolicy("notifications", "Notification module policy")
        );
        when(repository.findAll()).thenReturn(policies);

        // When
        List<ModulePolicy> result = modulePolicyService.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(policies);
        assertThat(result.get(0).getModuleName()).isEqualTo("users");
        assertThat(result.get(1).getModuleName()).isEqualTo("events");
        assertThat(result.get(2).getModuleName()).isEqualTo("notifications");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findAll should return empty list when no policies exist")
    void findAll_WhenNoPoliciesExist_ShouldReturnEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ModulePolicy> result = modulePolicyService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findAll should handle database access exceptions")
    void findAll_WhenDatabaseAccessFails_ShouldPropagateException() {
        // Given
        when(repository.findAll()).thenThrow(new DataAccessException("Database connection failed") {});

        // When & Then
        assertThatThrownBy(() -> modulePolicyService.findAll())
            .isInstanceOf(DataAccessException.class)
            .hasMessage("Database connection failed");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("save should persist module policy successfully")
    void save_ShouldPersistModulePolicy() {
        // Given
        ModulePolicy inputPolicy = createModulePolicy("testModule", "Test policy description");
        ModulePolicy savedPolicy = createModulePolicy("testModule", "Test policy description");
        savedPolicy.setId(1L);
        
        when(repository.save(inputPolicy)).thenReturn(savedPolicy);

        // When
        ModulePolicy result = modulePolicyService.save(inputPolicy);

        // Then
        assertThat(result).isEqualTo(savedPolicy);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getModuleName()).isEqualTo("testModule");
        verify(repository).save(inputPolicy);
    }

    @Test
    @DisplayName("save should handle null input gracefully")
    void save_WithNullInput_ShouldPassNullToRepository() {
        // Given
        when(repository.save(null)).thenReturn(null);

        // When
        ModulePolicy result = modulePolicyService.save(null);

        // Then
        assertThat(result).isNull();
        verify(repository).save(null);
    }

    @Test
    @DisplayName("save should handle data integrity violations")
    void save_WhenDataIntegrityViolation_ShouldPropagateException() {
        // Given
        ModulePolicy policy = createModulePolicy("duplicateName", "Duplicate policy");
        when(repository.save(policy))
            .thenThrow(new DataIntegrityViolationException("Duplicate key constraint violation"));

        // When & Then
        assertThatThrownBy(() -> modulePolicyService.save(policy))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessage("Duplicate key constraint violation");
        verify(repository).save(policy);
    }

    @Test
    @DisplayName("delete should remove policy by ID")
    void delete_ShouldRemovePolicyById() {
        // Given
        Long policyId = 1L;

        // When
        modulePolicyService.delete(policyId);

        // Then
        verify(repository).deleteById(policyId);
    }

    @Test
    @DisplayName("delete should handle null ID")
    void delete_WithNullId_ShouldPassNullToRepository() {
        // When
        modulePolicyService.delete(null);

        // Then
        verify(repository).deleteById(null);
    }

    @Test
    @DisplayName("delete should handle non-existent IDs gracefully")
    void delete_WithNonExistentId_ShouldNotThrowException() {
        // Given
        Long nonExistentId = 999L;
        doNothing().when(repository).deleteById(nonExistentId);

        // When & Then
        // Should not throw any exception
        modulePolicyService.delete(nonExistentId);
        
        verify(repository).deleteById(nonExistentId);
    }

    @Test
    @DisplayName("delete should handle database constraint violations")
    void delete_WhenConstraintViolation_ShouldPropagateException() {
        // Given
        Long policyId = 1L;
        doThrow(new DataIntegrityViolationException("Cannot delete: referenced by other entities"))
            .when(repository).deleteById(policyId);

        // When & Then
        assertThatThrownBy(() -> modulePolicyService.delete(policyId))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessage("Cannot delete: referenced by other entities");
        verify(repository).deleteById(policyId);
    }

    /**
     * Helper method to create ModulePolicy test instances
     */
    private ModulePolicy createModulePolicy(String moduleName, String description) {
        ModulePolicy policy = new ModulePolicy();
        policy.setModuleName(moduleName);
        // Note: ModulePolicy doesn't have description field, just moduleName and policy
        return policy;
    }

    /**
     * Helper method to create ModulePolicy with just name
     */
    private ModulePolicy createModulePolicy(String name) {
        return createModulePolicy(name, "Default description for " + name);
    }
}