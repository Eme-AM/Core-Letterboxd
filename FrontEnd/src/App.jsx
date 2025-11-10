import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Dashboard from './views/Dashboard';
import Messages from './views/Messages/Messages';
import Subscriptions from './views/Subscriptions';
import Login from './views/Login';
import './globals.scss';
import Configuration from './views/Configuration/Configuration';
import { ToastContainer } from 'react-toastify';

function EventDetails() {
  return <h2>EventDetails</h2>;
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/configuration" element={<Configuration />} />
          <Route path="/subscriptions" element={<Subscriptions />} />
          <Route path="/eventdetails" element={<EventDetails />} />
          <Route path="/messages" element={<Messages />} />
          <Route path="/login" element={<Login />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;


