package com.example.CoreBack.entity;

import com.example.CoreBack.testutils.EventTestDataFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para EventDTO
 * 
 * Verifica:
 * - Validaciones Bean Validation (@NotBlank, @NotNull)
 * - Getters y Setters funcionando correctamente
 * - Construcción de objetos válidos e inválidos
 * - Estructura de datos correcta
 */
class EventDTOTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    @DisplayName("EventDTO válido debe pasar todas las validaciones")
    void validEventDTO_ShouldPassAllValidations() {
        // Given
        EventDTO validEventDTO = EventTestDataFactory.createValidEventDTO();

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(validEventDTO);

        // Then
        assertThat(violations).isEmpty();
        assertThat(validEventDTO.getId()).isNotBlank();
        assertThat(validEventDTO.getType()).isNotBlank();
        assertThat(validEventDTO.getSource()).isNotBlank();
        assertThat(validEventDTO.getDatacontenttype()).isNotBlank();
        assertThat(validEventDTO.getData()).isNotNull();
        assertThat(validEventDTO.getSysDate()).isNotNull();
    }

    @Test
    @DisplayName("EventDTO con ID nulo debe fallar validación")
    void eventDTOWithNullId_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setId(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'id' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con ID vacío debe fallar validación")
    void eventDTOWithBlankId_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setId("");

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'id' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con tipo nulo debe fallar validación")
    void eventDTOWithNullType_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setType(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'type' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con tipo vacío debe fallar validación")
    void eventDTOWithBlankType_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setType("   "); // Solo espacios en blanco

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'type' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con source nulo debe fallar validación")
    void eventDTOWithNullSource_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setSource(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'source' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con datacontenttype nulo debe fallar validación")
    void eventDTOWithNullDataContentType_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setDatacontenttype(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'datacontenttype' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con data nulo debe fallar validación")
    void eventDTOWithNullData_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setData(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'data' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con sysDate nulo debe fallar validación")
    void eventDTOWithNullSysDate_ShouldFailValidation() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createValidEventDTO();
        eventDTO.setSysDate(null);

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'SysDate' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con múltiples campos nulos debe mostrar todas las validaciones")
    void eventDTOWithMultipleNullFields_ShouldShowAllValidations() {
        // Given
        EventDTO eventDTO = new EventDTO();
        // Todos los campos nulos por defecto

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).hasSize(6); // 6 campos obligatorios
        
        Set<String> violationMessages = Set.of(
            "El campo 'id' es obligatorio",
            "El campo 'type' es obligatorio", 
            "El campo 'source' es obligatorio",
            "El campo 'datacontenttype' es obligatorio",
            "El campo 'data' es obligatorio",
            "El campo 'SysDate' es obligatorio"
        );

        violations.forEach(violation -> {
            assertThat(violationMessages).contains(violation.getMessage());
        });
    }

    @Test
    @DisplayName("EventDTO debe permitir data con estructura compleja")
    void eventDTOWithComplexData_ShouldBeValid() {
        // Given
        EventDTO eventDTO = EventTestDataFactory.createEventDTOWithComplexData();

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // Then
        assertThat(violations).isEmpty();
        // Verificar que tiene los campos del EventTestDataFactory
        assertThat(eventDTO.getData()).containsKey("movieId");
        assertThat(eventDTO.getData()).containsKey("rating");
        assertThat(eventDTO.getData()).containsKey("review");
        assertThat(eventDTO.getData()).containsKey("tags");
        assertThat(eventDTO.getData()).containsKey("user");
    }

    @Test
    @DisplayName("Getters y Setters deben funcionar correctamente")
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Given
        EventDTO eventDTO = new EventDTO();
        LocalDateTime testDate = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        Map<String, Object> testData = Map.of("test", "value");

        // When
        eventDTO.setId("test-id");
        eventDTO.setType("test.type");
        eventDTO.setSource("test-source");
        eventDTO.setDatacontenttype("application/json");
        eventDTO.setSysDate(testDate);
        eventDTO.setData(testData);

        // Then
        assertThat(eventDTO.getId()).isEqualTo("test-id");
        assertThat(eventDTO.getType()).isEqualTo("test.type");
        assertThat(eventDTO.getSource()).isEqualTo("test-source");
        assertThat(eventDTO.getDatacontenttype()).isEqualTo("application/json");
        assertThat(eventDTO.getSysDate()).isEqualTo(testDate);
        assertThat(eventDTO.getData()).isEqualTo(testData);
    }

    @Test
    @DisplayName("EventDTO debe aceptar diferentes tipos de data")
    void eventDTOWithDifferentDataTypes_ShouldBeValid() {
        // Given & When & Then
        
        // Data con strings
        EventDTO stringDataEvent = EventTestDataFactory.createUserEvent("created");
        assertThat(validator.validate(stringDataEvent)).isEmpty();

        // Data con números
        EventDTO numericDataEvent = EventTestDataFactory.createRatingEvent();
        assertThat(validator.validate(numericDataEvent)).isEmpty();

        // Data con arrays
        EventDTO arrayDataEvent = EventTestDataFactory.createEventDTOWithComplexData();
        assertThat(validator.validate(arrayDataEvent)).isEmpty();

        // Data vacía (pero no null)
        EventDTO emptyDataEvent = EventTestDataFactory.createValidEventDTO();
        emptyDataEvent.setData(Map.of());
        assertThat(validator.validate(emptyDataEvent)).isEmpty();
    }

    @Test
    @DisplayName("Constructor por defecto debe crear objeto vacío")
    void defaultConstructor_ShouldCreateEmptyObject() {
        // When
        EventDTO eventDTO = new EventDTO();

        // Then
        assertThat(eventDTO.getId()).isNull();
        assertThat(eventDTO.getType()).isNull();
        assertThat(eventDTO.getSource()).isNull();
        assertThat(eventDTO.getDatacontenttype()).isNull();
        assertThat(eventDTO.getSysDate()).isNull();
        assertThat(eventDTO.getData()).isNull();
    }
}