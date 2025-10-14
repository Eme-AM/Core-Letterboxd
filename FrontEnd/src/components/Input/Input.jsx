import styles from './Input.module.scss';
import search from '../../assets/search.png';

export const Input = ({ placeholder = "Search by ID, type, origin, destination...", onChange, value, onKeyDown, isSearch }) => {
  return (
    <div className={styles.wrapper}>
      <input
        type="text"
        className={`${styles.input} ${isSearch && styles.searchInput}`}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onKeyDown={onKeyDown}
      />
      {isSearch && <img src={search} alt="Ver detalles" className={styles.icon} />}
    </div>
  );
};
