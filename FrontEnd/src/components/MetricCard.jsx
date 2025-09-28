import React from 'react';
import './MetricCard.css';

const MetricCard = ({ 
  title, 
  value, 
  change, 
  icon, 
  changeType = "neutral", 
  width = "auto", 
  height = "auto" 
}) => {
  return (
    <div 
      className={`metric-card ${change ? changeType : ""}`} 
      style={{ width, height }}
    >
      <div className="metric-header">
        <span>{title}</span>
        {icon && <span className="metric-icon">{icon}</span>}
      </div>

      <div className="metric-value">{value}</div>
      
      {/* Solo se renderiza si existe change */}
      {change && (
        <div className="metric-change">
          <span className={`change-badge ${changeType}`}>{change}</span>
          <span className="change-desc">From last month</span>
        </div>
      )}
    </div>
  );
};

export default MetricCard;
