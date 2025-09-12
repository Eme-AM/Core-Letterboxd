import styles from './SearchInput.module.scss';
import search from '../../assets/search.png';

export const SearchInput = ({ placeholder = "Search by ID, type, origin, destination...", onChange, value, onKeyDown }) => {
  return (
    <div className={styles.wrapper}>
      <input
        type="text"
        className={styles.input}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        onKeyDown={onKeyDown}
      />
      <img src={search} alt="Ver detalles" className={styles.icon} />
    </div>
  );
};
