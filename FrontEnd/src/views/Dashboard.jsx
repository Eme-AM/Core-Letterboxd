import React, { useEffect, useState } from 'react';
import './Dashboard.css';
import MetricCard from '../components/MetricCard';
import Sidebar from '../components/Sidebar';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
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

/*const eventsEvolutionData = [
  { time: '00hs', value: 400 },
  { time: '03hs', value: 50 },
  { time: '06hs', value: 20 },
  { time: '09hs', value: 200 },
  { time: '12hs', value: 250 },
  { time: '15hs', value: 400 },
  { time: '18hs', value: 800 },
  { time: '21hs', value: 950 },
  { time: '24hs', value: 400 },
]

const eventsPerModuleData = [
  { module: 'Users', value: 300 },
  { module: 'Movies', value: 500 },
  { module: 'Discovery', value: 200 },
  { module: 'Analytics', value: 700 },
];;*/

/*const recentEvents = [
  {
    id: "evt_0005", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46",
    payload: { movie: 'movie 1' }, timeline: [
      { name: "Created", timestamp: "2025-09-01 10:00" },
      { name: "Processed", timestamp: "2025-09-01 10:30" },
      { name: "Delivered", timestamp: "2025-09-01 11:00" }
    ]
  },
  {
    id: "evt_0004", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "In Queue", timestamp: "2025-08-17 15:30:46",
    payload: { movie: 'movie 2' }, timeline: [
      { name: "Created", timestamp: "2025-09-01 10:00" },
      { name: "Processed", timestamp: "2025-09-01 10:30" },
      { name: "In Queue", timestamp: "2025-09-01 11:00" }
    ]
  },
  {
    id: "evt_0003", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Failed", timestamp: "2025-08-17 15:30:46",
    payload: { movie: 'movie 3' }, timeline: [
      { name: "Created", timestamp: "2025-09-01 10:00" },
      { name: "Processed", timestamp: "2025-09-01 10:30" },
      { name: "Failed", timestamp: "2025-09-01 11:00" }
    ]
  },
  {
    id: "evt_0002", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46",
    payload: { movie: 'movie 4' }, timeline: [
      { name: "Created", timestamp: "2025-09-01 10:00" },
      { name: "Processed", timestamp: "2025-09-01 10:30" },
      { name: "Delivered", timestamp: "2025-09-01 11:00" }
    ]
  },
  {
    id: "evt_0001", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Delivered", timestamp: "2025-08-17 15:30:46",
    payload: { movie: 'movie 5' }, timeline: [
      { name: "Created", timestamp: "2025-09-01 10:00" },
      { name: "Processed", timestamp: "2025-09-01 10:30" },
      { name: "Delivered", timestamp: "2025-09-01 11:00" }
    ]
  },
];*/

function Dashboard() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null); // evento seleccionado para modal
  const [events, setEvents] = useState([]);
  const [eventsEvolutionData, setEventsEvolutionData] = useState([]);
  const [eventsPerModuleData, setEventsPerModuleData] = useState([]);
  const [stats, setStats] = useState([]);

  useEffect(() => {
    api
      //.get("events?page=0&size=5&module=movies&search=inception")
      .get(`events?size=5`)
      .then(res => {
        if (res.data) {
          setEvents(res.data.events);
        }
      })
      .catch(err => {
        //setError("No se pudieron cargar los eventos.");
      })
      .finally(() => {
        //setLoading(false);
      });
    api
      //.get("events?page=0&size=5&module=movies&search=inception")
      .get(`events/evolution`)
      .then(res => {
        if (res.data) {
          const formattedData = res.data.map(item => ({
            time: item.hour.toString().padStart(2, '0') + "hs",
            value: item.count
          }));

          setEventsEvolutionData(formattedData);
        }
      })
      .catch(err => {
        //setError("No se pudieron cargar los eventos.");
      })
      .finally(() => {
        //setLoading(false);
      });
    api
      //.get("events?page=0&size=5&module=movies&search=inception")
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
        //setError("No se pudieron cargar los eventos.");
      })
      .finally(() => {
        //setLoading(false);
      });
    api
      .get(`events/stats`)
      .then(res => {
        if (res.data) {
          const formattedData = [
            { title: "Total Events", value: res.data.totalEvents, change: res.data.totalChange, src: totalEventsSvg },
            { title: "Delivered", value: res.data.delivered, src: deliveredSvg, change: res.data.deliveredChange },
            { title: "Failed", value: res.data.failed, change: res.data.failedChange, src: failedSvg },
            { title: "In Queue", value: res.data.inQueue, src: inQueueSvg },
          ];
          setStats(formattedData)
        }
      })
      .catch(err => {
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
         {/*<MetricCard
            title="Total Events"
            value={stats.totalEvents}
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
          />*/}
          {stats.map((stat) => (
            <MetricCard
              title={stat.title}
              value={stat.value}
              change={stat.change || stat.change == 0 && `${stat.change > 0 ? '+' : stat.change < 0 ? '-' : ''}${stat.change}%`}
              changeType={stat.change <= 0 ? 'negative' : 'positive'}
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
