import styles from "./Table.module.scss";

function Table({ headers = [], data = [], renderRow }) {
    return (
        <div className={styles.table}>
            <div className={`${styles.row} ${styles.headerRow}`}>
                {headers.map((header, index) => (
                    <div key={index} className={styles.cell}
                        style={{ "--cols": headers.length }}>
                        {header}
                    </div>
                ))}
            </div>

            {data.length > 0 ?
                data.map((item, index) => {
                    const cells = renderRow(item, index);
                    return (
                        <div key={index} className={styles.row}
                            style={{ "--cols": headers.length }} >
                            {
                                cells.map((cellContent, i) => (
                                    <div key={i} className={styles.cell}>
                                        {cellContent}
                                    </div>
                                ))
                            }
                        </div>
                    );
                })
                :
                <div className={styles.row}
                    style={{ "--cols": 1 }} >
                    <div className={styles.cell}><b>No events found</b></div>
                </div>
            }
        </div >
    );
}

export default Table;
