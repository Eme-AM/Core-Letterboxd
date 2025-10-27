package com.example.CoreBack.service;

import com.example.CoreBack.entity.SystemConfig;
import com.example.CoreBack.repository.SystemConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SystemConfigService
 * 
 * Tests coverage:
 * - getConfig() with existing configuration
 * - getConfig() with non-existing configuration (fallback creation)
 * - update() method with ID forcing behavior
 */
@ExtendWith(MockitoExtension.class)
class SystemConfigServiceTest {

    @Mock
    private SystemConfigRepository repository;

    @InjectMocks
    private SystemConfigService systemConfigService;

    @Test
    @DisplayName("getConfig should return existing config when found")
    void getConfig_WhenConfigExists_ShouldReturnExistingConfig() {
        // Given
        SystemConfig existingConfig = new SystemConfig();
        existingConfig.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existingConfig));

        // When
        SystemConfig result = systemConfigService.getConfig();

        // Then
        assertThat(result).isEqualTo(existingConfig);
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).findById(1L);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("getConfig should create and save new config when not found")
    void getConfig_WhenConfigNotExists_ShouldCreateAndSaveNewConfig() {
        // Given
        SystemConfig newConfig = new SystemConfig();
        newConfig.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(repository.save(any(SystemConfig.class))).thenReturn(newConfig);

        // When
        SystemConfig result = systemConfigService.getConfig();

        // Then
        assertThat(result).isEqualTo(newConfig);
        verify(repository).findById(1L);
        verify(repository).save(any(SystemConfig.class));
    }

    @Test
    @DisplayName("update should force ID to 1L and save config")
    void update_ShouldForceIdAndSaveConfig() {
        // Given
        SystemConfig inputConfig = new SystemConfig();
        inputConfig.setId(999L); // Wrong ID that should be forced to 1L
        
        SystemConfig savedConfig = new SystemConfig();
        savedConfig.setId(1L);
        when(repository.save(any(SystemConfig.class))).thenReturn(savedConfig);

        // When
        SystemConfig result = systemConfigService.update(inputConfig);

        // Then
        assertThat(inputConfig.getId()).isEqualTo(1L); // ID should be forced
        assertThat(result).isEqualTo(savedConfig);
        verify(repository).save(inputConfig);
    }

    @Test
    @DisplayName("update should work correctly when input config has null ID")
    void update_WithNullId_ShouldSetIdToOneAndSave() {
        // Given
        SystemConfig inputConfig = new SystemConfig();
        inputConfig.setId(null); // Null ID that should be set to 1L
        
        SystemConfig savedConfig = new SystemConfig();
        savedConfig.setId(1L);
        when(repository.save(any(SystemConfig.class))).thenReturn(savedConfig);

        // When
        SystemConfig result = systemConfigService.update(inputConfig);

        // Then
        assertThat(inputConfig.getId()).isEqualTo(1L); // ID should be set to 1L
        assertThat(result).isEqualTo(savedConfig);
        verify(repository).save(inputConfig);
    }

    @Test
    @DisplayName("update should preserve config properties while forcing ID")
    void update_ShouldPreserveConfigPropertiesWhileForcingId() {
        // Given
        SystemConfig inputConfig = createSystemConfigWithProperties();
        inputConfig.setId(999L); // Wrong ID
        
        SystemConfig savedConfig = new SystemConfig();
        savedConfig.setId(1L);
        when(repository.save(any(SystemConfig.class))).thenReturn(savedConfig);

        // When
        SystemConfig result = systemConfigService.update(inputConfig);

        // Then
        assertThat(inputConfig.getId()).isEqualTo(1L); // ID should be forced
        verify(repository).save(inputConfig);
        // Verify the same instance was passed to save, preserving all properties
        assertThat(result).isEqualTo(savedConfig);
    }

    private SystemConfig createSystemConfigWithProperties() {
        SystemConfig config = new SystemConfig();
        // Add any properties that SystemConfig might have
        // This is a placeholder - adjust based on actual SystemConfig properties
        return config;
    }
}