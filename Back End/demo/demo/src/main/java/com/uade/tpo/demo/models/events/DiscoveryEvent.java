package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for discovery and recommendation operations
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DiscoveryEvent extends BaseEvent {
    
    private String action; // SEARCH, RECOMMEND, TRENDING_UPDATE, POPULAR_UPDATE
    private String searchQuery;
    private Map<String, Object> discoveryData;
    
    public DiscoveryEvent(String action, Map<String, Object> discoveryData) {
        super("DISCOVERY_EVENT", "DISCOVERY_SERVICE");
        this.action = action;
        this.discoveryData = discoveryData;
    }
    
    @Override
    public String getRoutingKey() {
        return "discovery." + action.toLowerCase();
    }
}
