import styles from "./GeneralConfiguration.module.scss";
import ContainerSection from '../../../components/ContainerSection/ContainerSection';
import ConfigurationItem from '../../../components/ConfigurationItem/ConfigurationItem';
import Button from '../../../components/Button/Button';
import { useState } from "react";

function GeneralConfiguration() {
    const [configuration, setConfiguration] = useState({
        threshold: '',
        latency: '',
        error: '',
        email: ''
    });

    const handleChange = (field, value) => {
        setConfiguration(prev => ({
            ...prev,
            [field]: value
        }));
    };

    const saveConfiguration = () => {

    }

    return (
        <ContainerSection title={'General Configuration'} subtitle={"Manage general configuration"}>
            <div className={styles.container}>
                <ConfigurationItem title={'Queue Threshold'} infoDetails={'Maximum number of queued messages before triggering an alert.'} placeholder={'1000'}
                    value={configuration.threshold} setValue={(val) => handleChange('threshold', val)} className={styles.item} />
                <ConfigurationItem title={'Latency Threshold (ms)'} infoDetails={'Maximum allowed processing latency (in milliseconds) before alerting.'} placeholder={'5000'}
                    value={configuration.latency} setValue={(val) => handleChange('latency', val)} className={styles.item} />
                <ConfigurationItem title={'Error Threshold (%)'} infoDetails={'Maximum allowed error rate (as a percentage) before alerting.'} placeholder={'5'}
                    value={configuration.error} setValue={(val) => handleChange('error', val)} className={styles.item} />
                <ConfigurationItem title={'Notifications Email'} infoDetails={'Email address where system alerts will be sent.'} placeholder={'admin0messagging@gmail.com'}
                    value={configuration.email} setValue={(val) => handleChange('email', val)} className={styles.item} />
                <div className={styles.btnWrapper}><Button text={'Save'} onClick={saveConfiguration} /></div>
            </div>
        </ContainerSection>
    );
}

export default GeneralConfiguration;
