import { toCapitalizeCase } from '../../functions';
import styles from './StateTag.module.scss';

function StateTag({ state = "InQueue" }) {

  const labels = {
    "InQueue": "queue",
    Delivered: "delivered",
    DELIVERED: "delivered",
    FAILED: "failed",
    Enabled: "delivered",
    Disabled: "failed",
  };
  return (
    <div className={`${styles.tag} ${styles[labels[state]]}`}>
      {toCapitalizeCase(state)}
    </div>
  );
}

export default StateTag;
