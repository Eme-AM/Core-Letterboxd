import styles from "./ConfigurationItem.module.scss";
import info from '../../assets/info.png';
import { Input } from '../Input/Input';
import ToggleSwitch from '../ToggleSwitch/ToggleSwitch';

function ConfigurationItem({ title, infoDetails, placeholder, value, setValue, className, isSwitch }) {

    return (
        <div className={`${styles.configItem} ${className}`}>
            <div className={`${styles.header} ${isSwitch && styles.headerSwitch}`}>
                <div>
                    <p className={!isSwitch && styles.title}>{title}</p>
                    {isSwitch && <ToggleSwitch initial={value} onChange={setValue} />}
                </div>
                <img src={info} alt="Info" className={styles.icon} title={infoDetails} />
            </div>
            {!isSwitch &&
                <Input
                    value={value}
                    onChange={(e) => setValue(e.target.value)}
                    placeholder={placeholder}
                />
            }
        </div>
    );
}

export default ConfigurationItem;
