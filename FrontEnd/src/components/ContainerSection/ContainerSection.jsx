import styles from './ContainerSection.module.scss';

function ContainerSection({ title, subtitle, children }) {
    return (
        <div className={styles.container}>
            <h3 className={styles.title}>{title}</h3>
            <p className={styles.subtitle}>{subtitle}</p>
            {children}
        </div>
    );
}

export default ContainerSection;
