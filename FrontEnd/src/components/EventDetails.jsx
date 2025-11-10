import React, { useEffect, useState } from "react";
import "./EventDetails.css";
import TimelineItem from "../views/TimelineItem/TimelineItem";
import deliveredSvg from '../assets/delivered.svg';
import failedSvg from '../assets/failed.svg';
import inQueueSvg from '../assets/inQueue.svg';
import { formatDateTime, toCapitalizeCase } from "../functions";
import api from "../axios";


function EventDetails({ event, onClose }) {

  const [activeTab, setActiveTab] = useState("details");
  const [timeline, setTimeline] = useState("details");

  useEffect(() => {
    if (event) {
      api
        .get(`events/${event.id}`)
        .then(res => {
          res.data && setTimeline(res.data.timeline);
        })
    }
  }, [event]);

  if (!event) return null;

  let statusIcon, statusIconStyle;
  const normalizedStatus = event.status?.toUpperCase();
  switch (normalizedStatus) {
    case 'DELIVERED':
      statusIcon = deliveredSvg;
      statusIconStyle = { filter: 'brightness(0) saturate(100%) invert(54%) sepia(77%) saturate(505%) hue-rotate(77deg) brightness(97%) contrast(101%)' };
      break;
    case 'FAILED':
      statusIcon = failedSvg;
      statusIconStyle = { filter: 'brightness(0) saturate(100%) invert(36%) sepia(99%) saturate(7492%) hue-rotate(340deg) brightness(97%) contrast(101%)' };
      break;
    case 'INQUEUE':
      statusIcon = inQueueSvg;
      statusIconStyle = { filter: 'brightness(0) saturate(100%) invert(81%) sepia(41%) saturate(7492%) hue-rotate(359deg) brightness(101%) contrast(101%)' };

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

            className={`status-badge ${event.status.toLowerCase()}`}

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

        <div
          className={`modal-content bg-content  ${activeTab === "details" ? "grid-2col" : ""
            }
           `}
        >
          {activeTab === "details" && (
            <>
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

              <div className="row">
                <span className="label">Type</span>
                <span className="value">{event.eventType}</span>
              </div>
              <div className="row">
                <span className="label">Date/Time</span>
                <span className="value">{formatDateTime(event.occurredAt)}</span>
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
                  timestamp={formatDateTime(t.time)}
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
