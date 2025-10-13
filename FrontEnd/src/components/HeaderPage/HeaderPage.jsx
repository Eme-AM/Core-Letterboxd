import appLogo from '../../assets/App Logo.png';

function HeaderSection({ title, subtitle }) {
    return (
        <header className="dashboard-header">
            <div className="dashboard-title-logo">
                <h1>{title}</h1>
                <img src={appLogo} alt="App Logo" className="logo-img" />
            </div>
            <p className="dashboard-desc">
                {subtitle}
            </p>
        </header>
    );
}

export default HeaderSection;