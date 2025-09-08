import React from "react";
import "./EventDetails.css";

function EventDetails({ event, onClose }) {
  if (!event) return null;

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
            <span className="status-icon">✔</span>
            <div>
              <div className="status-id">{event.id}</div>
              <div className="status-type">{event.action}</div>
            </div>
          </div>
          <span
            className={`status-badge ${
              event.status.toLowerCase().replace(" ", "-")
            }`}
          >
            {event.status}
          </span>
        </div>

        <div className="modal-tabs">
          <button className="tab active">Details</button>
          <button className="tab">Payload</button>
          <button className="tab">Timeline</button>
        </div>

        <div className="modal-content grid-2col">
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
        </div>
      </div>
    </div>
  );
}

export default EventDetails;
