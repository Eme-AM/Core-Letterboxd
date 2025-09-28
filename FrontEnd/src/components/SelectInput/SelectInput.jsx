import styles from './SelectInput.module.scss';
import dropdown from '../../assets/arrow-select.png';

export const SelectInput = ({ placeholder = "No Options", options = [], value, onChange }) => {
    return (
        <div className={styles.wrapper}>
            <select
                className={styles.select}
                value={value}
                onChange={onChange}
            >

                {options.length > 0 ?
                    options.map((opt, i) => (
                        <option key={i} value={opt.value}>
                            {opt.label}
                        </option>
                    )) :
                    <option value="">
                        {placeholder}
                    </option>
                }
            </select>
            <img src={dropdown} alt="Dropdown" className={styles.icon} />
        </div>
    );
};
