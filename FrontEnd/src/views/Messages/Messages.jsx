import { Link } from 'react-router-dom';
import Events from './Events/Events';
import ContainerSection from '../../components/ContainerSection/ContainerSection';
import Filters from './Filters/Filters';
import { useState } from 'react';
import Sidebar from '../../components/Sidebar';
import EventDetails from '../../components/EventDetails';

function Messages() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [searchFilter, setSearchFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [moduleFilter, setModuleFilter] = useState('');


  const headers = [
    "ID",
    "Event Type",
    "Origin",
    "Destination",
    "State",
    "Date/Time",
    "Details"
  ];

  const events = [
    {
      id: "evt_0005",
      action: "user.addFavourites",
      from: "Users",
      to: "Movies",
      status: "Delivered",
      timestamp: "2025-08-17 15:30:46",
      payload: { movie: 'movie 1' }, timeline: [
        { name: "Created", timestamp: "2025-09-01 10:00" },
        { name: "Processed", timestamp: "2025-09-01 10:30" },
        { name: "Delivered", timestamp: "2025-09-01 11:00" }
      ]
    },
    {
      id: "evt_0006",
      action: "user.removeFavourites",
      from: "Users",
      to: "Movies",
      status: "In Queue",
      timestamp: "2025-08-18 10:20:12",
      payload: { movie: 'movie 2' }, timeline: [
        { name: "Created", timestamp: "2025-09-01 10:00" },
        { name: "Processed", timestamp: "2025-09-01 10:30" },
        { name: "In Queue", timestamp: "2025-09-01 11:00" }
      ]
    }, {
      id: "evt_0004", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "In Queue", timestamp: "2025-08-17 15:30:46",
      payload: { movie: 'movie 3' }, timeline: [
        { name: "Created", timestamp: "2025-09-01 10:00" },
        { name: "Processed", timestamp: "2025-09-01 10:30" },
        { name: "In Queue", timestamp: "2025-09-01 11:00" }
      ]
    },
    {
      id: "evt_0003", action: "user.addFavourites", from: "Users Module", to: "Movies Module", status: "Failed", timestamp: "2025-08-17 15:30:46",
      payload: { movie: 'movie 4' }, timeline: [
        { name: "Created", timestamp: "2025-09-01 10:00" },
        { name: "Processed", timestamp: "2025-09-01 10:30" },
        { name: "Failed", timestamp: "2025-09-01 11:00" }
      ]
    },


  ];
  const filteredEvents = events.filter(event => {
    const matchesSearch =
      searchFilter === '' ||
      Object.entries(event)
        .filter(([key]) => key !== 'payload' && key !== 'timeline')
        .some(([_, val]) =>
          String(val).toLowerCase().includes(searchFilter.toLowerCase())
        );

    const matchesStatus = statusFilter === '' || event.status === statusFilter;
    const matchesModule = moduleFilter === '' || event.action === moduleFilter;

    return matchesSearch && matchesStatus && matchesModule;
  });

  return (

    <div className="dashboard-container">
      <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
      <main className={`dashboard-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
        <ContainerSection title={'Filters & Search'} subtitle={"Filter and search for specific events"}>
          <Filters
            searchFilter={searchFilter}
            setSearchFilter={setSearchFilter}
            statusFilter={statusFilter}
            setStatusFilter={setStatusFilter}
            moduleFilter={moduleFilter}
            setModuleFilter={setModuleFilter}
          />
        </ContainerSection>
        <ContainerSection title={'List of Events'} subtitle={"Events along the system’s history"}>
          <Events headers={headers} events={filteredEvents} setEvent={setSelectedEvent} />
        </ContainerSection>
      </main>
      {selectedEvent && (
        <EventDetails
          event={selectedEvent}
          onClose={() => setSelectedEvent(null)}
        />
      )}
    </div>
  );
}

export default Messages;
