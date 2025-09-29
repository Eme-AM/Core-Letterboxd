import React, { useState, useEffect } from 'react';
import './Dashboard.css';
import MetricCard from '../components/MetricCard';
import Sidebar from '../components/Sidebar';
import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar
} from 'recharts';
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
/*
const [metrics, setMetrics] = useState(null);
const [eventsEvolutionData, setEventsEvolutionData] = useState([]);
const [eventsPerModuleData, setEventsPerModuleData] = useState([]);
const [recentEvents, setRecentEvents] = useState([]);*/

/* DATOS HARDCODEADOS
const metrics = {
  totalEvents: { value: 847, change: '+12%' },
  delivered:   { value: 820, change: '+8%'  },
  failed:      { value: 22,  change: '-3%'  },
  inQueue:     { value: 5 }                 
};
 
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
 

  const API = 'http://core-letterboxd.us-east-2.elasticbeanstalk.com';
  //const API = 'http://localhost:8080'

  useEffect(() => {
    // 1) Stats
    api
      .get(`events/stats`)
      .then(res => {
        if (res.data) {
          const formattedData = [
            { title: "Total Events", value: res.data.totalEvents, change: toPercent(res.data.totalEventsChange), src: totalEventsSvg },
            { title: "Delivered", value: res.data.delivered, src: deliveredSvg, change: toPercent(res.data.deliveredChange) },
            { title: "Failed", value: res.data.failed, change: toPercent(res.data.failedChange), src: failedSvg },
            { title: "In Queue", value: res.data.inQueue, src: inQueueSvg },
          ];
          setStats(formattedData)
        }
      })
      .catch(err => {
        throw new Error(`HTTP ${statsRes.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    // 2) Evolution
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
        new Error(`HTTP ${evoRes.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    // 3) Per-module

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
        new Error(`HTTP ${modRes.status}`);
      })
      .finally(() => {
        //setLoading(false);
      });
    /*(async () => {
      try {
        const statsRes = await fetch(`${API}/events/stats`);
        if (!statsRes.ok) throw new Error(`HTTP ${statsRes.status}`);
        const statsData = await statsRes.json();
   
        const mappedMetrics = {
          totalEvents: { value: statsData.totalEvents, change: toPercent(statsData.totalEventsChange) },
          delivered: { value: statsData.delivered, change: toPercent(statsData.deliveredChange) },
          failed: { value: statsData.failed, change: toPercent(statsData.failedChange) },
          inQueue: { value: statsData.inQueue }
        };
        setMetrics(mappedMetrics);*/

    // 2) Evolution
    /*const evoRes = await fetch(`${API}/events/evolution`);
    if (!evoRes.ok) throw new Error(`HTTP ${evoRes.status}`);
    const evoJson = await evoRes.json();

    const evoArray = Array.isArray(evoJson)
      ? evoJson
      : Object.entries(evoJson).map(([hour, value]) => ({ hour: Number(hour), value: Number(value) }));

    const buckets = Array(8).fill(0);
    for (const item of evoArray) {
      const h = Number(item.hour);
      const val = Number(item.value) || 0;
      if (Number.isFinite(h) && h >= 0 && h < 24) {
        buckets[Math.floor(h / 3)] += val;
      }
    }
    const evoMapped = ['00hs', '03hs', '06hs', '09hs', '12hs', '15hs', '18hs', '21hs']
      .map((label, i) => ({ time: label, value: buckets[i] }));
    setEventsEvolutionData(evoMapped);*/

    //3
    /*
    const modRes = await fetch(`${API}/events/per-module`);
    if (!modRes.ok) throw new Error(`HTTP ${modRes.status}`);
    const modJson = await modRes.json();

    const modMapped = Array.isArray(modJson)
      ? modJson.map(({ module, value }) => ({ module, value: Number(value) || 0 }))
      : Object.entries(modJson).map(([module, value]) => ({ module, value: Number(value) || 0 }));

    setEventsPerModuleData(modMapped);
*/
    // 4) Recent Events
    
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
    /*const recentRes = await fetch(`${API}/events?page=0&size=5&module=movies&search=inception`);
    if (!recentRes.ok) throw new Error(`HTTP ${recentRes.status}`);
    const recentJson = await recentRes.json();

    const mappedRecent = recentJson.events.map(ev => {
      const payloadObj = JSON.parse(ev.payload || '{}');
      return {
        id: `evt_${ev.id}`,
        action: ev.eventType,
        from: ev.source,
        to: "Movies Module",
        status: ev.status,
        timestamp: ev.occurredAt,
        payload: payloadObj,
        timeline: [
          { name: "Created", timestamp: ev.occurredAt },
          { name: ev.status, timestamp: ev.occurredAt }
        ]
      };
    });
    setRecentEvents(mappedRecent);*/

  /*} catch (err) {
    console.error('Error obteniendo datos', err);
  }
}) ();*/
}, []);


  return (
    <div className="dashboard-container">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
      <main className={`dashboard-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
        <HeaderSection title={'Dashboard'} subtitle={'Monitoring & Management system’s events in real time'} />

        <section className="dashboard-metrics">
          {/*<MetricCard
            title="Total Events"
            value={metrics.totalEvents.value}
            change={metricValues.totalEvents.change}
            changeType={getChangeType(metricValues.totalEvents.change)}
            icon={<img src={totalEventsSvg} alt="Total Events" style={{ width: 40, height: 40 }} />}
          />
          <MetricCard
            title="Delivered"
            value={metrics.delivered.value}
            change={metrics.delivered.change}
            changeType={getChangeType(metricValues.delivered.change)}
            icon={<img src={deliveredSvg} alt="Delivered" style={{ width: 40, height: 40 }} />}
          />
          <MetricCard
            title="Failed"
            value={metrics.failed.value}
            change={metricValues.failed.change}
            changeType={getChangeType(metricValues.failed.change)}
            icon={<img src={failedSvg} alt="Failed" style={{ width: 40, height: 40 }} />}
          />
          <MetricCard
            title="In Queue"
            value={metrics.inQueue.value}
            icon={<img src={inQueueSvg} alt="In Queue" style={{ width: 40, height: 40 }} />}
          />*/}
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
