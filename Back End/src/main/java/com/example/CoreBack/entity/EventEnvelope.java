package com.example.CoreBack.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public class EventEnvelope {

    @NotBlank(message = "El campo 'type' es obligatorio")
    @Pattern(
        regexp = "^[a-z]+\\.[a-z]+\\.(created|updated|deleted)$",
        message = "El campo 'type' debe seguir el formato <dominio>.<entidad>.<acción>"
    )
    private String type;

    @NotBlank(message = "El campo 'source' es obligatorio")
    @Pattern(
        regexp = "^/[a-zA-Z0-9._-]+(/[a-zA-Z0-9._-]+)*$",
        message = "El campo 'source' debe tener formato tipo /modulo/api"
    )
    private String source;

    @NotBlank(message = "El campo 'datacontenttype' es obligatorio")
    @Pattern(
        regexp = "^application/json(;.*)?$",
        message = "El campo 'datacontenttype' debe ser application/json"
    )
    private String datacontenttype;

    @NotNull(message = "El campo 'sysDate' no puede ser nulo")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX]")
    private OffsetDateTime sysDate;

    @NotNull(message = "El campo 'data' no puede ser nulo")
    private JsonNode data;

    // --- Getters y setters ---
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDatacontenttype() { return datacontenttype; }
    public void setDatacontenttype(String datacontenttype) { this.datacontenttype = datacontenttype; }

    public OffsetDateTime getSysDate() { return sysDate; }
    public void setSysDate(OffsetDateTime sysDate) { this.sysDate = sysDate; }

    public JsonNode getData() { return data; }
    public void setData(JsonNode data) { this.data = data; }
}