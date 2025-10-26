import React, { useState } from 'react';
import HeaderSection from '../components/HeaderPage/HeaderPage';
import Sidebar from '../components/Sidebar';
import { HiOutlineUsers, HiOutlineFilm, HiOutlineShare, HiOutlineLocationMarker, HiOutlineHeart, HiOutlineChartBar } from 'react-icons/hi';
import { TbCompass } from 'react-icons/tb';
import './Subscriptions.scss';

function Subscriptions() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const modules = [
        {
            id: 1,
            title: 'Users',
            icon: <HiOutlineUsers />,
            events: '9 available events',
            description: 'User management, profiles, authenticate.',
            active: true
        },
        {
            id: 2,
            title: 'Movies',
            icon: <HiOutlineFilm />,
            events: '13 available events',
            description: 'Movie catalogue and metadata.',
            active: true
        },
        {
            id: 3,
            title: 'Social Graph & Activity Feed',
            icon: <HiOutlineShare />,
            events: '8 available events',
            description: 'Social connections and activities feed.',
            active: true
        },
        {
            id: 4,
            title: 'Discovery & Recomendations',
            icon: <TbCompass />,
            events: '9 available events',
            description: 'Discovery system and recomendations for users.',
            active: true
        },
        {
            id: 5,
            title: 'Reviews & Rating',
            icon: <HiOutlineHeart />,
            events: '12 available events',
            description: 'Content rating management.',
            active: true
        },
        {
            id: 6,
            title: 'Analytics & Insight',
            icon: <HiOutlineChartBar />,
            events: '15 available events',
            description: 'Data analysis and system metrics.',
            active: true
        }
    ];

    return (
        <div className="subscriptions-container">
            <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />
            <main className={`subscriptions-main ${isSidebarOpen ? "sidebar-open" : "sidebar-collapsed"}`}>
                <HeaderSection 
                    title="Subscriptions" 
                    subtitle="Explore and subscribe to events from different modules."
                />
                
                <div className="modules-grid">
                    {modules.map(module => (
                        <div key={module.id} className="module-card">
                            <div className="module-header">
                                <div className="module-icon">{module.icon}</div>
                                <div className="module-info">
                                    <h3 className="module-title">{module.title}</h3>
                                    <p className="module-events">{module.events}</p>
                                </div>
                                {module.active && <span className="active-badge">Active</span>}
                            </div>
                            
                            <p className="module-description">{module.description}</p>
                            
                            <button className="view-events-btn" style={{ backgroundColor: 'rgba(20, 106, 113, 1)' }}>View Events</button>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    );
}

export default Subscriptions;
