import React, { useState, useEffect } from 'react';
import './Dashboard.scss';
import MetricCard from '../components/MetricCard';
import Sidebar from '../components/Sidebar';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar} from 'recharts';
import totalEventsSvg from '../assets/totalEvents.svg';
import deliveredSvg from '../assets/delivered.svg';
import failedSvg from '../assets/failed.svg';
import inQueueSvg from '../assets/inQueue.svg';
import EventItem from "../components/EventItem";
import EventDetailsModal from "../components/EventDetails";
import HeaderSection from '../components/HeaderPage/HeaderPage';
import api from '../axios';

const ChartCard = ({ title, subtitle, children }) => (
  <div className="chart-card">
    <div className="chart-header">
      <span>{title}</span>
      <div className="chart-subtitle">{subtitle}</div>
    </div>
    <div className="chart-content">{children}</div>
  </div>
);

const getChangeType = (change) => {
  if (!change) return undefined;
  return String(change).trim().startsWith('-') ? 'negative' : 'positive';
};

const toPercent = (v) => {
  if (v === undefined || v === null || Number.isNaN(Number(v))) return undefined;
  const n = Number(v);
  const sign = n > 0 ? '+' : (n < 0 ? '' : '');
  return `${sign}${n}%`;
};

function Dashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null); 
  const [events, setEvents] = useState([]);
  const [eventsEvolutionData, setEventsEvolutionData] = useState([]);
  const [eventsPerModuleData, setEventsPerModuleData] = useState([]);
  const [stats, setStats] = useState([]);


  useEffect(() => {
    // 1) Stats
    api
      .get(`events/stats`)
      .then(res => {
        if (res.data) {
          const formattedData = [
            { title: "Total Events", value: res.data.totalEvents, change: toPercent(res.data.totalChange), src: totalEventsSvg },
            { title: "Delivered", value: res.data.delivered, src: deliveredSvg, change: toPercent(res.data.deliveredChange) },
            { title: "Failed", value: res.data.failed, change: toPercent(res.data.failedChange), src: failedSvg },
            { title: "In Queue", value: res.data.inQueue, src: inQueueSvg },
          ];
          setStats(formattedData)
        }
      })
      .catch(err => {
        throw new Error(`HTTP ${err.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    // 2) Evolution
    api
      .get(`events/evolution`)
      .then(res => {
        if (res.data) {
          const formattedData = res.data.map(item => {
            // Restar 3 horas y ajustar el rango
            const hourAdjusted = ((item.hour - 3 + 24) % 24);
            return {
              time: hourAdjusted.toString().padStart(2, '0') + "hs",
              value: item.count
            };
          });

          setEventsEvolutionData(formattedData);
        }
      })
      .catch(err => {
        new Error(`HTTP ${err.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    // 3) Per-module

    api
      .get(`events/per-module`)
      .then(res => {
        if (res.data) {
          const formattedData = Object.entries(res.data).map(([key, value]) => ({
            module: key,
            value: value
          }));

          setEventsPerModuleData(formattedData);
        }
      })
      .catch(err => {
        new Error(`HTTP ${err.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    // 4) Recent Events
    api
      .get(`events?size=5`)
      .then(res => {
        if (res.data) {
          setEvents(res.data.events);
        }
      })
      /*.catch(err => {
        //setError("No se pudieron cargar los eventos.");
      })
      .finally(() => {
        //setLoading(false);
      });
  }, []);

  return (
    <div className="dashboard-container">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
      <main className={`dashboard-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
        <HeaderSection title={'Dashboard'} subtitle={'Monitoring & Management system’s events in real time'} />

        <section className="dashboard-metrics">
          {stats.map((stat) => (
            <MetricCard
              title={stat.title}
              value={stat.value}
              change={stat.change || stat.change == 0 && `${stat.change > 0 ? '+' : stat.change < 0 ? '-' : ''}${stat.change}%`}
              changeType={getChangeType(stat.change)}
              icon={<img src={stat.src} alt={stat.title} style={{ width: 40, height: 40 }} />}
            />
          ))}
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
          {events.map((event, index) => (
            <EventItem
              key={index}
              {...event}
              onView={() => setSelectedEvent(event)}
            />
          ))}
        </section>
      </main>

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
