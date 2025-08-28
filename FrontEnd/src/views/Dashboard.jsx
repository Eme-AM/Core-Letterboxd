import React from 'react';
import { Link } from 'react-router-dom';
import './Dashboard.css';
import appLogo from '../assets/App Logo.png';

// Puedes crear estos componentes en archivos separados para mayor escalabilidad
const Sidebar = () => (
  <aside className="dashboard-sidebar">
    <div className="sidebar-icon active" />
    <div className="sidebar-icon" />
    <div className="sidebar-icon" />
    {/* ...otros iconos... */}
    <div className="sidebar-user">A</div>
  </aside>
);

const MetricCard = ({ title, value, change, icon, changeType }) => (
  <div className={`metric-card ${changeType}`}>
    <div className="metric-header">
      <span>{title}</span>
      {icon && <span className="metric-icon">{icon}</span>}
    </div>
    <div className="metric-value">{value}</div>
    <div className="metric-change">
      <span className={`change-badge ${changeType}`}>{change}</span>
      <span className="change-desc">From last month</span>
    </div>
  </div>
);

const ChartCard = ({ title, subtitle, children }) => (
  <div className="chart-card">
    <div className="chart-header">
      <span>{title}</span>
      <div className="chart-subtitle">{subtitle}</div>
    </div>
    <div className="chart-content">{children}</div>
  </div>
);

// Puedes usar una librería de gráficos como Chart.js o recharts para los gráficos reales
const DummyLineChart = () => (
  <svg width="100%" height="120">
    {/* ...gráfico de líneas simulado... */}
    <polyline
      fill="none"
      stroke="#00eaff"
      strokeWidth="2"
      points="0,100 40,40 80,90 120,80 160,60 200,30 240,80"
    />
    {/* ...ejes y etiquetas... */}
  </svg>
);

const DummyBarChart = () => (
  <svg width="100%" height="120">
    {/* ...gráfico de barras simulado... */}
    <rect x="10" y="80" width="30" height="40" fill="#00eaff" />
    <rect x="60" y="60" width="30" height="60" fill="#00eaff" />
    <rect x="110" y="90" width="30" height="30" fill="#00eaff" />
    <rect x="160" y="70" width="30" height="50" fill="#00eaff" />
    {/* ...ejes y etiquetas... */}
  </svg>
);

function Dashboard() {
  return (
    <div className="dashboard-container">
      <Sidebar />
      <main className="dashboard-main">
        <header className="dashboard-header">
          <div className="dashboard-title-logo">
            <h1>Dashboard</h1>
            <img src={appLogo} alt="App Logo" className="logo-img" />
          </div>
          <p className="dashboard-desc">
            Monitoring & Management system's events in real time
          </p>
        </header>
        <section className="dashboard-metrics">
          <MetricCard
            title="Total Events"
            value={847}
            change="+12%"
            icon={<span>📈</span>}
            changeType="positive"
          />
          <MetricCard
            title="Delivered"
            value={820}
            change="+8%"
            icon={<span>✔️</span>}
            changeType="positive"
          />
          <MetricCard
            title="Failed"
            value={22}
            change="-3%"
            icon={<span>❌</span>}
            changeType="negative"
          />
          <MetricCard
            title="In Queue"
            value={5}
            icon={<span>⏰</span>}
            changeType="neutral"
          />
        </section>
        <section className="dashboard-charts">
          <ChartCard
            title="Events Evolution"
            subtitle="Processed events tendency across the last 24hs"
          >
            <DummyLineChart />
          </ChartCard>
          <ChartCard
            title="Events Per Module"
            subtitle="Processed events distribution across the system modules"
          >
            <DummyBarChart />
          </ChartCard>
        </section>
        {/* ...puedes agregar la sección de eventos recientes aquí... */}
      </main>
    </div>
  );
}

function Home() {
  return (
    <div>
      <h1>Bienvenido a la Home</h1>
      <p>Esta es la página principal de tu aplicación.</p>
      <nav>
        <ul>
          <li><Link to="/configuracion">Configuracion</Link></li>
          <li><Link to="/eventdetails">EventDetails</Link></li>
          <li><Link to="/messages">Messages</Link></li>
        </ul>
      </nav>
    </div>
  );
}

export default Dashboard;

