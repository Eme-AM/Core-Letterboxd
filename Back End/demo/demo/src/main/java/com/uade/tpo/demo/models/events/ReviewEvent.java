package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for review and rating operations
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReviewEvent extends BaseEvent {
    
    private String reviewId;
    private String movieId;
    private String action; // CREATED, UPDATED, DELETED, LIKED, UNLIKED
    private Double rating;
    private Map<String, Object> reviewData;
    
    public ReviewEvent(String action, String reviewId, String movieId, Map<String, Object> reviewData) {
        super("REVIEW_EVENT", "REVIEWS_SERVICE");
        this.action = action;
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.reviewData = reviewData;
    }
    
    @Override
    public String getRoutingKey() {
        return "reviews." + action.toLowerCase();
    }
}
