import { SearchInput } from "../../../components/SearchInput/SearchInput";
import { SelectInput } from "../../../components/SelectInput/SelectInput";
import styles from "./Filters.module.scss";

function Filters({ searchFilter, setSearchFilter, statusFilter, setStatusFilter, moduleFilter, setModuleFilter }) {
    return (
        <div className={styles.container}>
            <SearchInput
                value={searchFilter}
                onChange={(e) => setSearchFilter(e.target.value)}
            />
            <SelectInput
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                options={[
                    { label: 'Every State', value: '' },
                    //{ label: 'In Queue', value: 'In Queue' },
                    { label: 'Received', value: 'Received' },
                    { label: 'Failed', value: 'Failed' },
                    { label: 'Delivered', value: 'Delivered' }
                ]}
            />
            <SelectInput
                value={moduleFilter}
                onChange={(e) => setModuleFilter(e.target.value)}
                options={[
                    { label: 'Every Module', value: '' },
                    { label: 'Users', value: 'Users' },
                    { label: 'Movies', value: 'Movies' }
                ]}
            />
        </div>
    );
}

export default Filters;
