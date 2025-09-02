import React from "react";
import { FaCheckCircle, FaClock, FaTimesCircle, FaEye } from "react-icons/fa";
import "./EventItem.css";

function EventItem({ id, action, from, to, status, timestamp }) {
  // Selección de icono y color según status
  const getStatusConfig = () => {
    switch (status) {
      case "Delivered":
        return { icon: <FaCheckCircle color="#2ecc71" size={24} />, badgeClass: "status-delivered" };
      case "In Queue":
        return { icon: <FaClock color="#f1c40f" size={24} />, badgeClass: "status-queue" };
      case "Failed":
        return { icon: <FaTimesCircle color="#e74c3c" size={24} />, badgeClass: "status-failed" };
      default:
        return { icon: <FaClock color="#95a5a6" size={24} />, badgeClass: "status-unknown" };
    }
  };

  const { icon, badgeClass } = getStatusConfig();

  return (
    <div className="event-item">
      <div className="event-left">
        <div className="event-icon">{icon}</div>
        <div className="event-info">
          <div className="event-id">{id}</div>
          <div className="event-action">{action}</div>
          <div className="event-flow">{from} → {to}</div>
        </div>
      </div>
      <div className="event-right">
        <span className={`event-status ${badgeClass}`}>{status}</span>
        <span className="event-timestamp">{timestamp}</span>
        <FaEye className="event-view" size={20} />
      </div>
    </div>
  );
}

export default EventItem;
