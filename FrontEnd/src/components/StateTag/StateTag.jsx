import { toCapitalizeCase } from '../../functions';
import styles from './StateTag.module.scss';

function StateTag({ state = "In Queue" }) {

  const labels = {
    "In Queue": "queue",
    RECEIVED: "delivered",
    FAILED: "failed",
  };
  return (
    <div className={`${styles.tag} ${styles[labels[state]]}`}>
      {toCapitalizeCase(state)}
    </div>
  );
}

export default StateTag;
