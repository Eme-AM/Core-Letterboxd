package com.example.CoreBack.entity;

import com.example.CoreBack.config.LenientOffsetDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class EventDTO {

    @NotBlank(message = "El campo 'type' es obligatorio")
    private String type;

    @NotBlank(message = "El campo 'source' es obligatorio")
    private String source;

    @NotBlank(message = "El campo 'datacontenttype' es obligatorio")
    private String datacontenttype;

    // ✅ Ahora acepta con o sin offset. Si falta, asume UTC (o el offset que configures)
    @JsonDeserialize(using = LenientOffsetDateTimeDeserializer.class)
    private OffsetDateTime sysDate;

    @NotNull(message = "El campo 'data' es obligatorio")
    private Map<String, Object> data;
}
