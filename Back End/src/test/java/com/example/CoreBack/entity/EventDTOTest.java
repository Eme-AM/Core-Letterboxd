package com.example.CoreBack.entity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.CoreBack.testutils.TestData;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

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
        EventDTO validEventDTO = TestData.Events.validEventDTO();

        // When
        Set<ConstraintViolation<EventDTO>> violations = validator.validate(validEventDTO);

        // Then
        assertThat(violations).isEmpty();
        assertThat(validEventDTO.getType()).isNotBlank();
        assertThat(validEventDTO.getSource()).isNotBlank();
        assertThat(validEventDTO.getDatacontenttype()).isNotBlank();
        assertThat(validEventDTO.getData()).isNotNull();
        assertThat(validEventDTO.getSysDate()).isNotNull(); // OffsetDateTime
    }

    @Test
    @DisplayName("EventDTO con tipo nulo debe fallar validación")
    void eventDTOWithNullType_ShouldFailValidation() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setType(null);

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'type' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con tipo vacío debe fallar validación")
    void eventDTOWithBlankType_ShouldFailValidation() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setType("   ");

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'type' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con source nulo debe fallar validación")
    void eventDTOWithNullSource_ShouldFailValidation() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setSource(null);

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'source' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con datacontenttype nulo debe fallar validación")
    void eventDTOWithNullDataContentType_ShouldFailValidation() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setDatacontenttype(null);

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'datacontenttype' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con data nulo debe fallar validación")
    void eventDTOWithNullData_ShouldFailValidation() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setData(null);

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("El campo 'data' es obligatorio");
    }

    @Test
    @DisplayName("EventDTO con sysDate nulo debe ser válido (campo opcional)")
    void eventDTOWithNullSysDate_ShouldBeValid() {
        EventDTO eventDTO = TestData.Events.validEventDTO();
        eventDTO.setSysDate(null); // OffsetDateTime null permitido

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).hasSize(0);
    }

    @Test
    @DisplayName("EventDTO con múltiples campos nulos debe mostrar todas las validaciones")
    void eventDTOWithMultipleNullFields_ShouldShowAllValidations() {
        EventDTO eventDTO = new EventDTO(); // todos nulos

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        // 4 campos obligatorios: type, source, datacontenttype, data (sysDate es opcional)
        assertThat(violations).hasSize(4);

        Set<String> violationMessages = Set.of(
            "El campo 'id' es obligatorio",            // si tu DTO no valida id, simplemente no aparecerá
            "El campo 'type' es obligatorio",
            "El campo 'source' es obligatorio",
            "El campo 'datacontenttype' es obligatorio",
            "El campo 'data' es obligatorio"
        );

        violations.forEach(v -> assertThat(violationMessages).contains(v.getMessage()));
    }

    @Test
    @DisplayName("EventDTO debe permitir data con estructura compleja")
    void eventDTOWithComplexData_ShouldBeValid() {
        EventDTO eventDTO = TestData.Events.complexEvent();

        Set<ConstraintViolation<EventDTO>> violations = validator.validate(eventDTO);

        assertThat(violations).isEmpty();
        assertThat(eventDTO.getData()).containsKey("movieId");
        assertThat(eventDTO.getData()).containsKey("rating");
        assertThat(eventDTO.getData()).containsKey("review");
        assertThat(eventDTO.getData()).containsKey("tags");
        assertThat(eventDTO.getData()).containsKey("user");
    }

    @Test
    @DisplayName("Getters y Setters deben funcionar correctamente")
    void gettersAndSetters_ShouldWorkCorrectly() {
        EventDTO eventDTO = new EventDTO();
        OffsetDateTime testDate = OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC);
        Map<String, Object> testData = Map.of("test", "value");

        eventDTO.setType("test.type");
        eventDTO.setSource("test-source");
        eventDTO.setDatacontenttype("application/json");
        eventDTO.setSysDate(testDate); // OffsetDateTime
        eventDTO.setData(testData);

        assertThat(eventDTO.getType()).isEqualTo("test.type");
        assertThat(eventDTO.getSource()).isEqualTo("test-source");
        assertThat(eventDTO.getDatacontenttype()).isEqualTo("application/json");
        assertThat(eventDTO.getSysDate()).isEqualTo(testDate);
        assertThat(eventDTO.getData()).isEqualTo(testData);
    }

    @Test
    @DisplayName("EventDTO debe aceptar diferentes tipos de data")
    void eventDTOWithDifferentDataTypes_ShouldBeValid() {
        EventDTO stringDataEvent = TestData.Events.userEvent("created");
        assertThat(validator.validate(stringDataEvent)).isEmpty();

        EventDTO numericDataEvent = TestData.Events.ratingEvent("user123", "movie456", 4.5);
        assertThat(validator.validate(numericDataEvent)).isEmpty();

        EventDTO arrayDataEvent = TestData.Events.complexEvent();
        assertThat(validator.validate(arrayDataEvent)).isEmpty();

        EventDTO emptyDataEvent = TestData.Events.validEventDTO();
        emptyDataEvent.setData(Map.of());
        assertThat(validator.validate(emptyDataEvent)).isEmpty();
    }

    @Test
    @DisplayName("Constructor por defecto debe crear objeto vacío")
    void defaultConstructor_ShouldCreateEmptyObject() {
        EventDTO eventDTO = new EventDTO();

        assertThat(eventDTO.getType()).isNull();
        assertThat(eventDTO.getSource()).isNull();
        assertThat(eventDTO.getDatacontenttype()).isNull();
        assertThat(eventDTO.getSysDate()).isNull(); // OffsetDateTime
        assertThat(eventDTO.getData()).isNull();
    }
}
