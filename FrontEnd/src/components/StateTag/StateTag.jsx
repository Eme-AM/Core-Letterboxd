import styles from './StateTag.module.scss';

function StateTag({ state = "In Queue" }) {
  const labels = {
    "In Queue": "queue",
    Delivered: "delivered",
    Failed: "failed",
  };
  return (
    <div className={`${styles.tag} ${styles[labels[state]]}`}>
      {state}
    </div>
  );
}

export default StateTag;
