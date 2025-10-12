import styles from "./ToggleSwitch.module.scss";

const ToggleSwitch = ({ onChange, initial = false }) => { 

  const handleClick = () => { 
    if (onChange) onChange(!initial);
  };

  return (
    <div
      className={`${styles.toggle} ${initial ? styles.active : ""}`}
      onClick={handleClick}
    >
      <div className={styles.circle}></div>
    </div>
  );
};

export default ToggleSwitch;
