import styles from './TimelineItem.module.scss';

function TimelineItem({ name, timestamp }) {

    const labels = {
        "In Queue": "queue", 
        Failed: "failed",
    };
    return (
        <span className={`${styles.container} `}>
            <div className={`${styles.point} ${styles[labels[name]]}`}></div>
            <div>
                <p>
                    {name}
                </p>
                <p className={styles.timestamp}>
                    {timestamp}
                </p>

            </div>
        </span>
    );
}

export default TimelineItem;
