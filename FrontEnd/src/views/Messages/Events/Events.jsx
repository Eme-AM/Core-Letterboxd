import { Link } from "react-router-dom";
import styles from "./Events.module.scss";
import StateTag from "../../../components/StateTag/StateTag";
import eyeIcon from '../../../assets/eye.png';
import EventTypeTag from "../../../components/EventTypeTag/EventTypeTag";
import { toCapitalizeCase } from "../../../functions";

function Events({ events, headers, setEvent }) { 
  return (
    <div className={styles.table}>
      <div className={`${styles.row} ${styles.headerRow}`}>
        {headers.map((header, index) => (
          <div key={index} className={styles.cell}>
            {header}
          </div>
        ))}
      </div>

      {events.map((event, index) => (
        <div key={index} className={styles.row}>
          <div className={styles.cell}>{event.id.toString().padStart(4, '0')}</div>
          <div className={styles.cell}>
            <EventTypeTag>{event.eventType}</EventTypeTag>
          </div>
          <div className={styles.cell}>{toCapitalizeCase(event.source.replace("/", " ").replace("/api", ""))}</div>
          {/*<div className={styles.cell}>Usersevent.to.replace(" Module", "")</div>*/}
          <div className={styles.cell}>
            <StateTag state={event.status} />
          </div>
          <div className={styles.cell}><div className={styles.dateTime}>{event.occurredAt.replace("T", " ")}</div></div>
          <div className={styles.cell}>
            <img src={eyeIcon} alt="Ver detalles" className={styles.icon} onClick={()=>setEvent(event)}/>
          </div>
        </div>
      ))}
    </div>
  );
}

export default Events;
