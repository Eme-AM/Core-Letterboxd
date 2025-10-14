import React from "react";
import styles from "./Button.module.scss";

const Button = ({ text, onClick, isSecondary }) => {
  return (
    <button className={`${styles.button} ${isSecondary && styles.secondary}`} onClick={onClick}>
      {text}
    </button>
  );
};

export default Button;
