import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import {
  ChevronRight,
  ChevronLeft,
  LayoutDashboard,
  MessageSquare,
  Settings,
} from "lucide-react";
import "./Sidebar.css";

function Sidebar({ isOpen, setIsOpen }) {
  const location = useLocation();
  const { user } = useAuth();
  const toggleSidebar = () => setIsOpen(!isOpen);

  const menuItems = [
    { name: "Dashboard", path: "/", icon: LayoutDashboard },
    { name: "Messages", path: "/messages", icon: MessageSquare },
    { name: "Configuration", path: "/configuration", icon: Settings },
  ];

  const getUserInitials = () => {
    if (!user) return "A";

    if (user.name) {
      return user.name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .substring(0, 2);
    }

    if (user.username) {
      return user.username.substring(0, 2).toUpperCase();
    }

    if (user.email) {
      return user.email.substring(0, 2).toUpperCase();
    }

    return "U";
  };

  const getUserName = () => {
    if (!user) return "User";
    return user.name || user.username || user.email || "User";
  };

  const getUserAvatar = () => {
    if (user?.avatar || user?.profile_picture || user?.image) {
      return user.avatar || user.profile_picture || user.image;
    }
    return null;
  };

  const avatarUrl = getUserAvatar();

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

      <div className="sidebar-user">
        <div className="sidebar-user-avatar">
          {avatarUrl ? (
            <img
              src={avatarUrl}
              alt={getUserName()}
              className="sidebar-user-image"
            />
          ) : (
            getUserInitials()
          )}
        </div>
        {isOpen && <span className="sidebar-user-name">{getUserName()}</span>}
      </div>
    </aside>
  );
}

export default Sidebar;
