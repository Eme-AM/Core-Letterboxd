package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for movie-related operations
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MovieEvent extends BaseEvent {
    
    private String movieId;
    private String action; // CREATED, UPDATED, DELETED
    private Map<String, Object> movieData;
    
    public MovieEvent(String action, String movieId, Map<String, Object> movieData) {
        super("MOVIE_EVENT", "MOVIES_SERVICE");
        this.action = action;
        this.movieId = movieId;
        this.movieData = movieData;
    }
    
    @Override
    public String getRoutingKey() {
        return "movies." + action.toLowerCase();
    }
}
