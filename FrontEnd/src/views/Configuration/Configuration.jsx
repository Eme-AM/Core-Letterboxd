import ContainerSection from '../../components/ContainerSection/ContainerSection';
import { useEffect, useState } from 'react';
import Sidebar from '../../components/Sidebar';
import EventDetails from '../../components/EventDetails';
import styles from "./Configuration.module.scss";
import HeaderSection from '../../components/HeaderPage/HeaderPage';
import Policy from './Policy/Policy';
import Table from '../../components/Table/Table';
import edit from '../../assets/edit.png';
import deleteIcon from '../../assets/delete.png';
import StateTag from '../../components/StateTag/StateTag';
import SwitcherSection from './SwitcherSection/SwitcherSection';
import GeneralConfiguration from './GeneralConfiguration/GeneralConfiguration';
import { SelectInput } from '../../components/SelectInput/SelectInput';
import ConfirmDeleteModal from '../../components/ConfirmDeleteModal/ConfirmDeleteModal';
import api from '../../axios';
import { toast } from 'react-toastify';

function Configuration() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);
    const [showPolicies, setShowPolicies] = useState(true);
    const [deletePolicy, setDeletePolicy] = useState(null);
    const [contReload, setContReload] = useState(0);

    const [policy, setPolicy] = useState({});
    const [policyEdit, setPolicyEdit] = useState(null);
    const [policies, setPolicies] = useState([
        {
            id: 1,
            name: 'Nombre',
            minDelay: 1000,
            maxDelay: 30000,
            maxTries: 2,
            backoffMultiplier: 2,
            enabled: false
        },
        {
            id: 2,
            name: 'Nombre 2',
            minDelay: 1500,
            maxDelay: 4000,
            maxTries: 2,
            backoffMultiplier: 2.5,
            enabled: true
        }
    ]);
    const [modules, setModules] = useState([
        {
            name: 'Movies',
            policy: 1
        },
        {
            name: 'Discovery',
            policy: 2
        },
        {
            name: 'Users'
        }
    ]);

    useEffect(() => {
        api
             .get(`config/policies/retry`)
             .then(res => {
                 if (res.data) {
                     setPolicies(res.data);
                 }
             })
             /*.catch(err => {
                 //setError("No se pudieron cargar los eventos.");
             })
             .finally(() => {
                 //setLoading(false);
             });*/
    }, [contReload]);

    const handlePolicyChange = (index, newValue) => {
        //Call Back
        setModules(prevModules =>
            prevModules.map((m) =>
                m.name === index ? { ...m, policy: newValue || undefined } : m
            )
        );
    };

    const handleDelete = () => {
        api
            .delete(`config/policies/retry/${deletePolicy.id}`)
            .then(() => {
                toast.success("Policy deleted!");
                setPolicies(prevPolicies =>
                    prevPolicies.filter(policy => policy.id !== deletePolicy.id)
                );

                setDeletePolicy(null);
            })
            .catch(() => {
                toast.error("Error deleting policy!");
            })
            .finally(() => {
                //setLoading(false);
            });
    };

    const reloadPolicies = () => {
        setContReload(contReload + 1);
    }

    return (

        <div className="dashboard-container">
            <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
            <main className={`dashboard-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
                <HeaderSection title={'Configuration'} subtitle={'Monitoring & Management system’s events in real time'} />
                <SwitcherSection value={showPolicies} onChange={setShowPolicies} />
                {showPolicies ? (
                    <>
                        <Policy policy={policy} setPolicy={setPolicy} reloadPolicies={reloadPolicies} />
                        <ContainerSection title={'Existing Policies'} subtitle={"Events along the system’s history"}>
                            <Table
                                headers={["Name", "Max Tries", "Delay", "Enabled", "Multiplier", "Actions"]}
                                data={policies}
                                renderRow={(policy) => [
                                    policy.name,
                                    policy.maxTries,
                                    policy.minDelay + 'ms - ' + policy.maxDelay + 'ms',
                                    <StateTag state={policy.enabled ? 'Enabled' : 'Disabled'} />,
                                    policy.backoffMultiplier + 'x',
                                    <>
                                        <a href='#policy'>
                                            <img
                                                src={edit}
                                                alt="Edit policy"
                                                className={styles.icon}
                                                onClick={() => setPolicyEdit(policy)}
                                            />
                                        </a>
                                        <img
                                            src={deleteIcon}
                                            alt="Delete policy"
                                            className={styles.deleteIcon}
                                            onClick={() => setDeletePolicy(policy)}
                                        />
                                    </>
                                ]}
                            />
                        </ContainerSection>
                        {policyEdit &&
                            <div id='policy'>
                                <Policy policy={policyEdit} setPolicy={setPolicyEdit} reloadPolicies={reloadPolicies} />
                            </div>
                        }
                        <ContainerSection title={'Modules'} subtitle={"Policies asigned to each module."}>
                            <Table
                                headers={["Module", "Policy Asigned"]}
                                data={modules}
                                renderRow={(module) => [
                                    module.name,
                                    <SelectInput
                                        darkBg={true}
                                        value={module.policy}
                                        onChange={(e) => handlePolicyChange(module.name, e.target.value)}
                                        options={[
                                            ...(module.policy ? [] : [{ label: 'No Policy', value: '' }]),
                                            ...policies.map(policy => ({
                                                label: policy.name,
                                                value: policy.id
                                            }))
                                        ]}
                                    />
                                ]}
                            />
                            {/*value={moduleFilter}
                                        onChange={(e) => setModuleFilter(e.target.value)}*/}
                        </ContainerSection>
                    </>
                ) : (
                    <GeneralConfiguration />
                )}
            </main>
            {/*selectedEvent && (
                <EventDetails
                    event={selectedEvent}
                    onClose={() => setSelectedEvent(null)}
                />
            )*/}
            {deletePolicy && (
                <ConfirmDeleteModal
                    itemName={deletePolicy.name}
                    onConfirm={handleDelete}
                    onCancel={() => setDeletePolicy(null)}
                />
            )}
        </div>
    );
}

export default Configuration;
