import styles from "./Policy.module.scss";
import ContainerSection from '../../../components/ContainerSection/ContainerSection';
import ConfigurationItem from '../../../components/ConfigurationItem/ConfigurationItem';
import Button from '../../../components/Button/Button';
import api from "../../../axios";
import { toast } from 'react-toastify';

function Policy({ policy, setPolicy, reloadPolicies }) {

  const createPolicy = () => {
    const method = policy.id ? api.put : api.post;
    const url = policy.id
      ? `config/policies/retry/${policy.id}`
      : `config/policies/retry`;

    method(url, policy)
      .then(res => {
        if (res.data) {
          toast.success(policy.id ? "Policy updated successfully!" : "Policy created successfully!");

          setPolicy(policy.id ? null : {
            id: undefined,
            name: '',
            backoffMultiplier: '',
            minDelay: '',
            maxDelay: '',
            maxTries: '',
            enabled: false
          });
          reloadPolicies();
        }
      })
      .catch(err => {
        toast.error("Error saving policy!");
      })
      .finally(() => {
        //setLoading(false);
      });
  }

  const handleChange = (field, value) => {
    setPolicy(prev => ({
      ...prev,
      [field]: value
    }));
  };
  console.log(policy)
  return (
    <ContainerSection title={policy.id ? 'Edit Retry Policy' : 'New Retry Policy'} subtitle={"Configure the retry policies for different event types"}>
      <div className={styles.container}>
        <ConfigurationItem title={'Policy name'} infoDetails={'Unique name used to identify the retry policy.'} placeholder={'Standar Retry'}
          value={policy.name} setValue={(val) => handleChange('name', val)} className={styles.item} />
        <ConfigurationItem title={'Backoff Multiplier'} infoDetails={'Factor by which the retry delay increases after each failed attempt.'} placeholder={'2.0'}
          value={policy.minDelay} setValue={(val) => handleChange('minDelay', val)} className={styles.item} />
        <ConfigurationItem title={'Min delay (ms)'} infoDetails={'Minimum wait time (in milliseconds) before the first retry attempt.'} placeholder={'1000'}
          value={policy.maxDelay} setValue={(val) => handleChange('maxDelay', val)} className={styles.item} />
        <ConfigurationItem title={'Max delay (ms)'} infoDetails={'Maximum wait time (in milliseconds) allowed between retries.'} placeholder={'3000'}
          value={policy.maxTries} setValue={(val) => handleChange('maxTries', val)} className={styles.item} />
        <ConfigurationItem title={'Max tries'} infoDetails={'Total number of retry attempts before giving up.'} placeholder={'5'}
          value={policy.backoffMultiplier} setValue={(val) => handleChange('backoffMultiplier', val)} className={styles.item} />
        <ConfigurationItem title={'Policy Enabled'} infoDetails={'Enables or disables this retry policy.'} placeholder={'5'}
          value={policy.enabled} setValue={(val) => handleChange('enabled', val)} className={styles.item} isSwitch={true} />
        <div className={styles.btnWrapper}>
          <Button text={policy.id ? 'Save' : 'Create New'} onClick={createPolicy} />
          {policy.id && <div className={styles.btnCancelWrapper}><Button text={'Cancel'} isSecondary={true} onClick={() => setPolicy(null)} /></div>}
        </div>
      </div>
    </ContainerSection>
  );
}

export default Policy;
