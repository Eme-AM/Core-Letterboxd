package com.example.CoreBack.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

public class EventDTO {

    @NotBlank(message = "El campo 'id' es obligatorio")
    private String id;  // ID del evento que mandan los módulos

    @NotBlank(message = "El campo 'type' es obligatorio")
    private String type; // Ej: "user.created"

    @NotBlank(message = "El campo 'source' es obligatorio")
    private String source; // Ej: "/users/signup"

    @NotBlank(message = "El campo 'datacontenttype' es obligatorio")
    private String datacontenttype; // Ej: "application/json"

    @NotNull(message = "El campo 'SysDate' es obligatorio")
    private LocalDateTime sysDate; // Fecha y hora del evento

    @NotNull(message = "El campo 'data' es obligatorio")
    private Map<String, Object> data; // Payload dinámico

    public EventDTO() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDatacontenttype() { return datacontenttype; }
    public void setDatacontenttype(String datacontenttype) { this.datacontenttype = datacontenttype; }

    public LocalDateTime getSysDate() { return sysDate; }
    public void setSysDate(LocalDateTime sysDate) { this.sysDate = sysDate; }

    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}

