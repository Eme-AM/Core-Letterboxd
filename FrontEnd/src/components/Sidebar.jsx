import React, { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import {
  ChevronRight,
  ChevronLeft,
  LayoutDashboard,
  MessageSquare,
  Settings,
} from "lucide-react";
import "./Sidebar.css";

function Sidebar() {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();

  const toggleSidebar = () => setIsOpen(!isOpen);

  const menuItems = [
    { name: "Dashboard", path: "/", icon: LayoutDashboard },
    { name: "Messages", path: "/messages", icon: MessageSquare },
    { name: "Configuration", path: "/configuration", icon: Settings },
  ];

  return (
    <aside className={`sidebar ${isOpen ? "open" : "collapsed"}`}>
      <div className="sidebar-header">
        {isOpen && <h2 className="sidebar-title">Messaging Hub</h2>}
        <button className="sidebar-toggle" onClick={toggleSidebar}>
          {isOpen ? (
            <ChevronLeft className="sidebar-arrow" />
          ) : (
            <ChevronRight className="sidebar-arrow" />
          )}
        </button>
      </div>

      <div className="sidebar-divider" />

      <nav className="sidebar-menu">
        {menuItems.map((item) => {
          const isActive = location.pathname === item.path;
          const Icon = item.icon;
          return (
            <Link
              key={item.name}
              to={item.path}
              className={`sidebar-item ${isActive ? "active" : ""}`}
            >
              <div className="sidebar-icon">
                <Icon size={22} />
              </div>
              {isOpen && <span className="sidebar-text">{item.name}</span>}
            </Link>
          );
        })}
      </nav>

      <div className="sidebar-divider" />

      <div className="sidebar-user">
        <div className="sidebar-user-avatar">A</div>
        {isOpen && <span className="sidebar-user-name">Admin User</span>}
      </div>
    </aside>
  );
}

export default Sidebar;
