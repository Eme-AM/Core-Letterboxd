import React, { useState } from "react";
import styles from "./ToggleSwitch.module.scss";

const ToggleSwitch = ({ onChange, initial = false }) => {
  const [active, setActive] = useState(initial);

  const handleClick = () => {
    const newState = !active;
    setActive(newState);
    if (onChange) onChange(newState);
  };

  return (
    <div
      className={`${styles.toggle} ${active ? styles.active : ""}`}
      onClick={handleClick}
    >
      <div className={styles.circle}></div>
    </div>
  );
};

export default ToggleSwitch;
