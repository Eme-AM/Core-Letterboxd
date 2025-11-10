package com.example.CoreBack.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LenientOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter OFFSET_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter LOCAL_FMT  = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank()) return null;

        // 1) Intentar con offset (Z, -03:00, etc.)
        try {
            return OffsetDateTime.parse(text, OFFSET_FMT);
        } catch (DateTimeParseException ignore) {}

        // 2) Si no trae offset, parsear como LocalDateTime y asumir UTC (cambialo si querés -03:00)
        try {
            LocalDateTime ldt = LocalDateTime.parse(text, LOCAL_FMT);
            return ldt.atOffset(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            // 3) Reintento “relajado” por si vienen milisegundos u otros detalles menores
            try {
                // ISO_LOCAL_DATE_TIME ya soporta fracción; si igual falla, probamos parsear como Instant sin zona
                Instant instant = Instant.parse(text);
                return instant.atOffset(ZoneOffset.UTC);
            } catch (Exception ex) {
                // Dejá que Jackson muestre el error estándar si nada funcionó
                throw ctxt.weirdStringException(text, OffsetDateTime.class,
                        "Formato de fecha no reconocido; se esperaba ISO con o sin offset (ej: 2025-10-13T15:43:00 o 2025-10-13T15:43:00-03:00)");
            }
        }
    }
}
