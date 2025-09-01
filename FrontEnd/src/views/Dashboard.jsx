import React from 'react';
import './Dashboard.css';
import MetricCard from '../components/MetricCard';
import Sidebar from '../components/Sidebar';

import appLogo from '../assets/App Logo.png';
import totalEventsPng from '../assets/totalEvents.png';
import deliveredPng from '../assets/delivered.png';
import failedPng from '../assets/failed.png';
import inQueuePng from '../assets/inQueue.png';

const ChartCard = ({ title, subtitle, children }) => (
  <div className="chart-card">
    <div className="chart-header">
      <span>{title}</span>
      <div className="chart-subtitle">{subtitle}</div>
    </div>
    <div className="chart-content">{children}</div>
  </div>
);

const DummyLineChart = () => (
  <svg width="100%" height="120">
    <polyline
      fill="none"
      stroke="#00eaff"
      strokeWidth="2"
      points="0,100 40,40 80,90 120,80 160,60 200,30 240,80"
    />
  </svg>
);

const DummyBarChart = () => (
  <svg width="100%" height="120">
    <rect x="10" y="80" width="30" height="40" fill="#00eaff" />
    <rect x="60" y="60" width="30" height="60" fill="#00eaff" />
    <rect x="110" y="90" width="30" height="30" fill="#00eaff" />
    <rect x="160" y="70" width="30" height="50" fill="#00eaff" />
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
            icon={<img src={totalEventsPng} alt="Total Events" style={{width: 32, height: 32}} />}
            changeType="positive"
          />
          <MetricCard
            title="Delivered"
            value={820}
            change="+8%"
            icon={<img src={deliveredPng} alt="Delivered" style={{width: 32, height: 32}} />}
            changeType="positive"
          />
          <MetricCard
            title="Failed"
            value={22}
            change="-3%"
            icon={<img src={failedPng} alt="Failed" style={{width: 32, height: 32}} />}
            changeType="negative"
          />
          <MetricCard
            title="In Queue"
            value={5}
            icon={<img src={inQueuePng} alt="In Queue" style={{width: 32, height: 32}} />}
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
      </main>
    </div>
  );
}

export default Dashboard;
