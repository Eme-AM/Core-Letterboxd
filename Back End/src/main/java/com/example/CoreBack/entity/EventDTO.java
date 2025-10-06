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
    
    // Convenience methods for test compatibility
    public void setEventType(String eventType) { this.type = eventType; }
    public String getEventType() { return this.type; }
    
    public void setUserId(String userId) {
        if (this.data == null) {
            this.data = new java.util.HashMap<>();
        }
        this.data.put("userId", userId);
    }
    
    public String getUserId() {
        return this.data != null ? (String) this.data.get("userId") : null;
    }
    
    public void setMovieId(String movieId) {
        if (this.data == null) {
            this.data = new java.util.HashMap<>();
        }
        this.data.put("movieId", movieId);
    }
    
    public String getMovieId() {
        return this.data != null ? (String) this.data.get("movieId") : null;
    }
    
    public void setRating(double rating) {
        if (this.data == null) {
            this.data = new java.util.HashMap<>();
        }
        this.data.put("rating", rating);
    }
    
    public Double getRating() {
        return this.data != null ? (Double) this.data.get("rating") : null;
    }
    
    public void setEventData(String eventData) {
        if (this.data == null) {
            this.data = new java.util.HashMap<>();
        }
        this.data.put("eventData", eventData);
    }
    
    public String getEventData() {
        return this.data != null ? (String) this.data.get("eventData") : null;
    }
    
    public void setReview(String review) {
        if (this.data == null) {
            this.data = new java.util.HashMap<>();
        }
        this.data.put("review", review);
    }
    
    public String getReview() {
        return this.data != null ? (String) this.data.get("review") : null;
    }
}
