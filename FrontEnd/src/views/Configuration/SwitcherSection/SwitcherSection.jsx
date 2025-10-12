import React from "react";
import styles from "./SwitcherSection.module.scss";

const SwitcherSection = ({ value, onChange }) => {
  return (
    <div className={styles.switch}>
      <button
        className={`${styles.option} ${value ? styles.active : ""}`}
        onClick={() => onChange(true)}
      >
        Retry Policies
      </button>
      <button
        className={`${styles.option} ${!value ? styles.active : ""}`}
        onClick={() => onChange(false)}
      >
        System
      </button>
    </div>
  );
};

export default SwitcherSection;
