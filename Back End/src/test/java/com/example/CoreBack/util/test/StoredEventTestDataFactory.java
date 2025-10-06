package com.example.CoreBack.util.test;

import com.example.CoreBack.entity.StoredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Factory para crear datos de prueba para StoredEvent.
 * 
 * Proporciona métodos estáticos para generar objetos StoredEvent
 * con datos válidos para usar en tests de repositorio y persistencia.
 */
public class StoredEventTestDataFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Crea un StoredEvent básico válido con valores por defecto.
     * @return StoredEvent válido para testing
     */
    public static StoredEvent createValidStoredEvent() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 12345L);
        payload.put("email", "test@example.com");
        payload.put("username", "testuser");
        
        return new StoredEvent(
            UUID.randomUUID().toString(),
            "user.created",
            "/users/signup",
            "application/json",
            mapToJson(payload),
            LocalDateTime.now()
        );
    }

    /**
     * Crea un StoredEvent para eventos de creación de usuarios.
     * @param userId ID del usuario
     * @param email email del usuario
     * @param username nombre de usuario
     * @return StoredEvent para user.created
     */
    public static StoredEvent createUserCreatedStoredEvent(Long userId, String email, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("email", email);
        payload.put("username", username);
        
        return new StoredEvent(
            UUID.randomUUID().toString(),
            "user.created",
            "/users/signup",
            "application/json",
            mapToJson(payload),
            LocalDateTime.now()
        );
    }

    /**
     * Crea un StoredEvent para eventos de creación de películas.
     * @param movieId ID de la película
     * @param title título de la película
     * @param year año de lanzamiento
     * @return StoredEvent para movie.created
     */
    public static StoredEvent createMovieCreatedStoredEvent(Long movieId, String title, Integer year) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("movieId", movieId);
        payload.put("title", title);
        payload.put("year", year);
        payload.put("genre", "Drama");
        
        return new StoredEvent(
            UUID.randomUUID().toString(),
            "movie.created",
            "/movies/add",
            "application/json",
            mapToJson(payload),
            LocalDateTime.now()
        );
    }

    /**
     * Crea un StoredEvent con eventId específico para testing.
     * @param eventId ID específico del evento
     * @return StoredEvent con ID personalizado
     */
    public static StoredEvent createStoredEventWithId(String eventId) {
        StoredEvent event = createValidStoredEvent();
        event.setEventId(eventId);
        return event;
    }

    /**
     * Crea un StoredEvent con timestamp específico para testing.
     * @param timestamp timestamp específico
     * @return StoredEvent con timestamp personalizado
     */
    public static StoredEvent createStoredEventWithTimestamp(LocalDateTime timestamp) {
        StoredEvent event = createValidStoredEvent();
        event.setOccurredAt(timestamp);
        return event;
    }

    /**
     * Crea un StoredEvent con tipo específico para testing.
     * @param eventType tipo de evento personalizado
     * @return StoredEvent con tipo personalizado
     */
    public static StoredEvent createStoredEventWithType(String eventType) {
        StoredEvent event = createValidStoredEvent();
        event.setEventType(eventType);
        return event;
    }

    /**
     * Crea un StoredEvent con payload JSON específico.
     * @param payload mapa de datos para el payload
     * @return StoredEvent con payload personalizado
     */
    public static StoredEvent createStoredEventWithPayload(Map<String, Object> payload) {
        StoredEvent event = createValidStoredEvent();
        event.setPayload(mapToJson(payload));
        return event;
    }

    /**
     * Convierte un Map a JSON string para el payload.
     * @param map mapa de datos
     * @return JSON string
     */
    private static String mapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }

    /**
     * Crea múltiples StoredEvents para testing de listas.
     * @param count número de eventos a crear
     * @return array de StoredEvents
     */
    public static StoredEvent[] createMultipleStoredEvents(int count) {
        StoredEvent[] events = new StoredEvent[count];
        for (int i = 0; i < count; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", 1000L + i);
            payload.put("email", "user" + i + "@example.com");
            payload.put("username", "user" + i);
            
            events[i] = new StoredEvent(
                UUID.randomUUID().toString(),
                "user.created",
                "/users/signup",
                "application/json",
                mapToJson(payload),
                LocalDateTime.now().minusMinutes(i)
            );
        }
        return events;
    }
}
