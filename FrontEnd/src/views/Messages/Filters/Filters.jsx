import { Input } from "../../../components/Input/Input";
import { SelectInput } from "../../../components/SelectInput/SelectInput";
import styles from "./Filters.module.scss";

function Filters({ searchFilter, setSearchFilter, statusFilter, setStatusFilter, moduleFilter, setModuleFilter }) {
    return (
        <div className={styles.container}>
            <Input
                value={searchFilter}
                onChange={(e) => setSearchFilter(e.target.value)}
                isSearch={true}
            />
            <SelectInput
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                options={[
                    { label: 'Every State', value: '' }, 
                    { label: 'In Queue', value: 'InQueue' },
                    { label: 'Failed', value: 'Failed' },
                    { label: 'Delivered', value: 'Delivered' }
                ]}
            />
            <SelectInput
                value={moduleFilter}
                onChange={(e) => setModuleFilter(e.target.value)}
                options={[
                    { label: 'Every Module', value: '' },
                    { label: 'User', value: 'User' },
                    { label: 'Movie', value: 'Movie' },
                    { label: 'Discovery', value: 'Discovery' }
                ]}
            />
        </div>
    );
}

export default Filters;
