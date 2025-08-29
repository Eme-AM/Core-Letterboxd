package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for social interactions
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SocialEvent extends BaseEvent {
    
    private String targetUserId;
    private String action; // FOLLOW, UNFOLLOW, LIKE, UNLIKE, COMMENT, DELETE_COMMENT
    private String contentId;
    private String contentType; // REVIEW, MOVIE, USER
    private Map<String, Object> socialData;
    
    public SocialEvent(String action, String targetUserId, String contentId, String contentType, Map<String, Object> socialData) {
        super("SOCIAL_EVENT", "SOCIAL_SERVICE");
        this.action = action;
        this.targetUserId = targetUserId;
        this.contentId = contentId;
        this.contentType = contentType;
        this.socialData = socialData;
    }
    
    @Override
    public String getRoutingKey() {
        return "social." + action.toLowerCase();
    }
}
