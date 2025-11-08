package com.example.CoreBack.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.Map;

public class EventDTO {

    @NotBlank(message = "El campo 'type' es obligatorio")
    @Pattern(regexp = "^[a-z]+\\.[a-z]+\\.(created|updated|deleted)$",
             message = "type debe seguir el formato <dominio>.<entidad>.<acción>")
    private String type;

    @NotBlank(message = "El campo 'source' es obligatorio")
    @Pattern(regexp = "^/[a-zA-Z0-9._-]+(/[a-zA-Z0-9._-]+)*$",
             message = "source debe tener formato tipo /modulo/api")
    private String source;

    @NotBlank(message = "El campo 'datacontenttype' es obligatorio")
    @Pattern(regexp = "^application/json(;.*)?$",
             message = "datacontenttype debe ser application/json")
    private String datacontenttype;

    @NotNull(message = "El campo 'sysDate' es obligatorio")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[XXX]")
    private OffsetDateTime sysDate; // ✅ corregido

    @NotNull(message = "El campo 'data' es obligatorio")
    private Map<String, Object> data;

    public EventDTO() {}

    // === Getters y Setters ===
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDatacontenttype() { return datacontenttype; }
    public void setDatacontenttype(String datacontenttype) { this.datacontenttype = datacontenttype; }

    public OffsetDateTime getSysDate() { return sysDate; }   // ✅ actualizado
    public void setSysDate(OffsetDateTime sysDate) { this.sysDate = sysDate; } // ✅ actualizado

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}