import React, { useState } from 'react';
import './Dashboard.css';
import MetricCard from '../components/MetricCard';
import Sidebar from '../components/Sidebar';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import appLogo from '../assets/App Logo.png';
import totalEventsSvg from '../assets/totalEvents.svg';
import deliveredSvg from '../assets/delivered.svg';
import failedSvg from '../assets/failed.svg';
import inQueueSvg from '../assets/inQueue.svg';
import EventItem from "../components/EventItem";
import EventDetailsModal from "../components/EventDetails"; 

const ChartCard = ({ title, subtitle, children }) => (
  <div className="chart-card">
    <div className="chart-header">
      <span>{title}</span>
      <div className="chart-subtitle">{subtitle}</div>
    </div>
    <div className="chart-content">{children}</div>
  </div>
);

const eventsEvolutionData = [
  { time: '00hs', value: 400 },
  { time: '03hs', value: 50 },
  { time: '06hs', value: 20 },
  { time: '09hs', value: 200 },
  { time: '12hs', value: 250 },
  { time: '15hs', value: 400 },
  { time: '18hs', value: 800 },
  { time: '21hs', value: 950 },
  { time: '24hs', value: 400 },
];

const eventsPerModuleData = [
  { module: 'Users', value: 300 },
  { module: 'Movies', value: 500 },
  { module: 'Discovery', value: 200 },
  { module: 'Analytics', value: 700 },
];

const recentEvents = [
  { id: "evt_0005", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46" },
  { id: "evt_0004", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "In Queue", timestamp: "2025-08-17 15:30:46" },
  { id: "evt_0003", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Failed", timestamp: "2025-08-17 15:30:46" },
  { id: "evt_0002", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46" },
  { id: "evt_0001", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46" },
];

function Dashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null); // evento seleccionado para modal

  return (
    <div className="dashboard-container">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
      <main className={`dashboard-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
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
            icon={<img src={totalEventsSvg} alt="Total Events" style={{ width: 40, height: 40 }} />}
            changeType="positive"
          />
          <MetricCard
            title="Delivered"
            value={820}
            change="+8%"
            icon={<img src={deliveredSvg} alt="Delivered" style={{ width: 40, height: 40 }} />}
            changeType="positive"
          />
          <MetricCard
            title="Failed"
            value={22}
            change="-3%"
            icon={<img src={failedSvg} alt="Failed" style={{ width: 40, height: 40 }} />}
            changeType="negative"
          />
          <MetricCard
            title="In Queue"
            value={5}
            icon={<img src={inQueueSvg} alt="In Queue" style={{ width: 40, height: 40 }} />}
          />
        </section>

        <section className="dashboard-charts">
          <ChartCard
            title="Events Evolution"
            subtitle="Processed events tendency across the last 24hs"
          >
            <ResponsiveContainer width="100%" height={250}>
              <LineChart data={eventsEvolutionData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e3748" />
                <XAxis dataKey="time" stroke="#b0b8c1" />
                <YAxis stroke="#b0b8c1" />
                <Tooltip />
                <Line type="monotone" dataKey="value" stroke="#00eaff" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </ChartCard>

          <ChartCard
            title="Events Per Module"
            subtitle="Processed events distribution across the system modules"
          >
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={eventsPerModuleData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e3748" />
                <XAxis dataKey="module" stroke="#b0b8c1" />
                <YAxis stroke="#b0b8c1" />
                <Tooltip />
                <Bar dataKey="value" fill="#00eaff" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </ChartCard>
        </section>

        <section className="dashboard-recent-events">
          <h2 className="recent-title">Recent Events</h2>
          <p className="recent-subtitle">Last processed events by the system</p>
          {recentEvents.map((event, index) => (
            <div key={index} onClick={() => setSelectedEvent(event)} style={{ cursor: "pointer" }}>
              <EventItem {...event} />
            </div>
          ))}
        </section>
      </main>

      {/* Modal de detalles */}
      {selectedEvent && (
        <EventDetailsModal
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
        />
      )}
    </div>
  );
}

export default Dashboard;
