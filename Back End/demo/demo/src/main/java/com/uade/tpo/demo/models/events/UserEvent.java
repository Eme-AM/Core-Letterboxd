package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for user-related operations
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserEvent extends BaseEvent {
    
    private String targetUserId;
    private String action; // REGISTERED, UPDATED, DELETED, LOGIN, LOGOUT
    private Map<String, Object> userData;
    
    public UserEvent(String action, String targetUserId, Map<String, Object> userData) {
        super("USER_EVENT", "USERS_SERVICE");
        this.action = action;
        this.targetUserId = targetUserId;
        this.userData = userData;
    }
    
    @Override
    public String getRoutingKey() {
        return "users." + action.toLowerCase();
    }
}
