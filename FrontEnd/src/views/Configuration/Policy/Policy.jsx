import styles from "./Policy.module.scss";
import ContainerSection from '../../../components/ContainerSection/ContainerSection';
import ConfigurationItem from '../../../components/ConfigurationItem/ConfigurationItem';
import Button from '../../../components/Button/Button';

function Policy({ policy, setPolicy }) {

  const createPolicy = () => {

  }

  return (
    <ContainerSection title={'New Retry Policy'} subtitle={"Configure the retry policies for different event types"}>
      <div className={styles.container}>
        <ConfigurationItem title={'Policy name'} infoDetails={'Unique name used to identify the retry policy.'} placeholder={'Standar Retry'} value={'d'} setValue={setPolicy} className={styles.item} />
        <ConfigurationItem title={'Backoff Multiplier'} infoDetails={'Factor by which the retry delay increases after each failed attempt.'} placeholder={'2.0'} value={'d'} setValue={setPolicy} className={styles.item} />
        <ConfigurationItem title={'Min delay (ms)'} infoDetails={'Minimum wait time (in milliseconds) before the first retry attempt.'} placeholder={'1000'} value={'d'} setValue={setPolicy} className={styles.item} />
        <ConfigurationItem title={'Max delay (ms)'} infoDetails={'Maximum wait time (in milliseconds) allowed between retries.'} placeholder={'3000'} value={'d'} setValue={setPolicy} className={styles.item} />
        <ConfigurationItem title={'Max tries'} infoDetails={'Total number of retry attempts before giving up.'} placeholder={'5'} value={'d'} setValue={setPolicy} className={styles.item} />
        <ConfigurationItem title={'Policy Enabled'} infoDetails={'Enables or disables this retry policy.'} placeholder={'5'} value={'d'} setValue={setPolicy} className={styles.item} isSwitch={true} />
        <div className={styles.btnWrapper}><Button text={'Create New'} onClick={createPolicy} /></div>
      </div>
    </ContainerSection>
  );
}

export default Policy;
