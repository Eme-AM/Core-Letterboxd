import styles from './EventTypeTag.module.scss';

function EventTypeTag({ children }) {

    return (
        <span className={`${styles.tag} `}>
            {children}
        </span>
    );
}

export default EventTypeTag;
