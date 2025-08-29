package com.uade.tpo.demo.models.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Event for analytics and insights
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AnalyticsEvent extends BaseEvent {
    
    private String action; // USER_ACTIVITY, CONTENT_INTERACTION, SYSTEM_METRIC
    private String metricType;
    private Object metricValue;
    private Map<String, Object> analyticsData;
    
    public AnalyticsEvent(String action, String metricType, Object metricValue, Map<String, Object> analyticsData) {
        super("ANALYTICS_EVENT", "ANALYTICS_SERVICE");
        this.action = action;
        this.metricType = metricType;
        this.metricValue = metricValue;
        this.analyticsData = analyticsData;
    }
    
    @Override
    public String getRoutingKey() {
        return "analytics." + action.toLowerCase();
    }
}
