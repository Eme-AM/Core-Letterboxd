import React, { useState } from "react";
import "./EventDetails.css";
import TimelineItem from "../views/TimelineItem/TimelineItem";

function EventDetails({ event, onClose }) {
  if (!event) return null;

  const [activeTab, setActiveTab] = useState("details");

  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <header className="modal-header">
          <h2 className="modal-title">Event Details</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </header>

        <p className="modal-subtitle">
          Complete information about event “{event.id}”
        </p>

        <div className="modal-status">
          <div className="status-left">
            <span className="status-icon" style={{ marginLeft: 12 }}>
            </span>
            <div>
              <div className="status-id">{event.id}</div>
              <div className="status-type">{event.action}</div>
            </div>
          </div>
          <span
            className={`status-badge ${event.status.toLowerCase().replace(" ", "-")}`}
          >
            {event.status}
          </span>
        </div>

        {/* Tabs */}
        <div className="modal-tabs">
          <button
            className={`tab ${activeTab === "details" ? "active" : ""}`}
            onClick={() => setActiveTab("details")}
          >
            Details
          </button>
          <button
            className={`tab ${activeTab === "payload" ? "active" : ""}`}
            onClick={() => setActiveTab("payload")}
          >
            Payload
          </button>
          <button
            className={`tab ${activeTab === "timeline" ? "active" : ""}`}
            onClick={() => setActiveTab("timeline")}
          >
            Timeline
          </button>
        </div>

        {/* Content */}
        <div
          className={`modal-content bg-content  ${
            activeTab === "details" ? "grid-2col" : ""
          }
           `}
        >
          {activeTab === "details" && (
            <>
              {/* Columna izquierda */}
              <div className="row">
                <span className="label">Event’s ID</span>
                <span className="value">{event.id.replace("evt_", "")}</span>
              </div>
              <div className="row">
                <span className="label">Origin</span>
                <span className="value">{event.from}</span>
              </div>
              <div className="row">
                <span className="label">State</span>
                <span className="value">{event.status}</span>
              </div>

              {/* Columna derecha */}
              <div className="row">
                <span className="label">Type</span>
                <span className="value">{event.action}</span>
              </div>
              <div className="row">
                <span className="label">Destination</span>
                <span className="value">{event.to}</span>
              </div>
              <div className="row">
                <span className="label">Date/Time</span>
                <span className="value">{event.timestamp}</span>
              </div>
            </>
          )}

          {activeTab === "payload" && (
            <pre className="payload-content">
              {JSON.stringify(event.payload, null, 2)}
            </pre>
          )}

          {activeTab === "timeline" && (<div className="timeline-wrapper">
            {event.timeline && event.timeline.length > 0 ? (
              event.timeline.map((t, index) => (
                <TimelineItem
                  key={index}
                  name={t.name}
                  timestamp={t.timestamp}
                />
              ))
            ) : (
              <p>No timeline data available.</p>
            )}
          </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default EventDetails;
