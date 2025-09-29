import React, { useEffect, useState } from "react";
import "./EventDetails.css";
import TimelineItem from "../views/TimelineItem/TimelineItem";
import deliveredSvg from '../assets/delivered.svg';
import failedSvg from '../assets/failed.svg';
import inQueueSvg from '../assets/inQueue.svg';
import { toCapitalizeCase } from "../functions";
import api from "../axios";


function EventDetails({ event, onClose }) {
  if (!event) return null;


  const [activeTab, setActiveTab] = useState("details");
  const [timeline, setTimeline] = useState("details");


  useEffect(() => {
    if (event !== null) {
      api 
        .get(`events/${event.id}`)
        .then(res => {
          res.data && setTimeline(res.data.timeline);
        })
        .catch(err => {
          //setError("No se pudieron cargar los eventos.");
        })
        .finally(() => {
          //setLoading(false);
        });
    }
  }, [event]);

  // Selección de ícono SVG y filtro de color según status
  let statusIcon, statusIconStyle;
  switch (event.status) {
    case 'RECEIVED':
      statusIcon = deliveredSvg;
      statusIconStyle = { filter: 'invert(56%) sepia(77%) saturate(453%) hue-rotate(90deg) brightness(92%) contrast(92%)' };
      break;
    case 'FAILED':
      statusIcon = failedSvg;
      statusIconStyle = { filter: 'invert(34%) sepia(99%) saturate(7492%) hue-rotate(357deg) brightness(97%) contrast(101%)' };
      break;
    case 'In Queue':
      statusIcon = inQueueSvg;
      statusIconStyle = { filter: 'invert(24%) sepia(76%) saturate(1802%) hue-rotate(359deg) brightness(103%) contrast(105%)' };

      break;
    default:
      statusIcon = null;
      statusIconStyle = {};
  }


  return (
    <div className="modal-overlay">
      <div className="modal-container">
        <header className="modal-header">
          <h2 className="modal-title">Event Details</h2>
          <button className="modal-close" onClick={onClose}>✕</button>
        </header>

        <p className="modal-subtitle">
          Complete information about event “{"evt_" + event.id.toString().padStart(4, '0')}”
        </p>

        <div className="modal-status">
          <div className="status-left">
            <span className="status-icon" style={{ marginLeft: 12 }}>
              {statusIcon && (
                <img src={statusIcon} alt={event.status} style={{ width: 36, height: 36, ...statusIconStyle }} />
              )}
            </span>
            <div>
              <div className="status-id">{"evt_" + event.id.toString().padStart(4, '0')}</div>
              <div className="status-type">{event.eventType}</div>
            </div>
          </div>
          <span

            className={`status-badge ${event.status.toLowerCase().replace(" ", "-")}`}

          >
            {toCapitalizeCase(event.status)}
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
          className={`modal-content bg-content  ${activeTab === "details" ? "grid-2col" : ""
            }
           `}
        >
          {activeTab === "details" && (
            <>
              {/* Columna izquierda */}
              <div className="row">
                <span className="label">Event’s ID</span>
                <span className="value">{event.id}</span>
              </div>
              <div className="row">
                <span className="label">Origin</span>
                <span className="value">{toCapitalizeCase(event.source.replace("/", " ").replace("/api", ""))}</span>
              </div>
              <div className="row">
                <span className="label">State</span>
                <span className="value">{toCapitalizeCase(event.status)}</span>
              </div>

              {/* Columna derecha */}
              <div className="row">
                <span className="label">Type</span>
                <span className="value">{event.eventType}</span>
              </div>
              {/*<div className="row">
                <span className="label">Destination</span>
                <span className="value">{/*event.to* /}</span>
              </div></div>*/}
              <div className="row">
                <span className="label">Date/Time</span>
                <span className="value">{event.occurredAt.replace("T", " ")}</span>
              </div>
            </>
          )}

          {activeTab === "payload" && (
            <pre className="payload-content">
              {JSON.stringify(JSON.parse(event.payload), null, 2)}
            </pre>
          )}

          {activeTab === "timeline" && (<div className="timeline-wrapper">
            {timeline && timeline.length > 0 ? (
              timeline.map((t, index) => (
                <TimelineItem
                  key={index}
                  name={t.step}
                  timestamp={t.time.replace("T", " ")}
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
