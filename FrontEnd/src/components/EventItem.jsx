import deliveredSvg from '../assets/delivered.svg';
import failedSvg from '../assets/failed.svg';
import inQueueSvg from '../assets/inQueue.svg';
import eyeEventItem from '../assets/eyeEventItem.svg';
import "./EventItem.css";
import { formatDateTime, toCapitalizeCase } from "../functions";

function EventItem({ id, eventType, source, status, occurredAt, onView }) {
  // Selección de icono SVG y filtro de color según status
  const getStatusConfig = () => {
    switch (status) {
      case "DELIVERED":
        return {
          icon: <img src={deliveredSvg} alt="Delivered" className="event-status-icon" style={{ filter: 'brightness(0) saturate(100%) invert(54%) sepia(77%) saturate(505%) hue-rotate(77deg) brightness(97%) contrast(101%)' }} />,
          badgeClass: "status-delivered"
        };
      case "InQueue":
        return {
          icon: <img src={inQueueSvg} alt="In Queue" className="event-status-icon" style={{ filter: 'brightness(0) saturate(100%) invert(81%) sepia(41%) saturate(7492%) hue-rotate(359deg) brightness(101%) contrast(101%)' }} />,
          badgeClass: "status-queue"
        };
      case "FAILED":
        return {
          icon: <img src={failedSvg} alt="Failed" className="event-status-icon" style={{ filter: 'brightness(0) saturate(100%) invert(36%) sepia(99%) saturate(7492%) hue-rotate(340deg) brightness(97%) contrast(101%)' }} />,
          badgeClass: "status-failed"
        };
      default:
        return {
          icon: <img src={inQueueSvg} alt="Unknown" className="event-status-icon" style={{ filter: 'brightness(0) saturate(100%) invert(60%) sepia(0%) saturate(0%) hue-rotate(0deg) brightness(80%) contrast(80%)' }} />,
          badgeClass: "status-unknown"
        };
    }
  };

  const { icon, badgeClass } = getStatusConfig();

  return (
    <div className="event-item">
      <div className="event-left">
        <div className="event-icon">{icon}</div>
        <div className="event-info">
          <div className="event-id">{"evt_" + id.toString().padStart(4, '0')}</div>
          <div className="event-action">{eventType}</div>
          <div className="event-flow">{toCapitalizeCase(source.replace("/", " ").replace("/api", "")) + " Module"}{/* → {to*/}</div>
        </div>
      </div>
      <div className="event-right">
        <span className={`event-status ${badgeClass}`}>{toCapitalizeCase(status)}</span>
        <span className="event-timestamp">{formatDateTime(occurredAt)}</span>
        <img src={eyeEventItem} alt="View Event" className="event-view" onClick={onView} style={{ cursor: "pointer" }} />
      </div>
    </div>
  );
}

export default EventItem;
