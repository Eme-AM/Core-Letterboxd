import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './views/Dashboard';

function Configuracion() {
  return <h2>Configuracion</h2>;
}

function EventDetails() {
  return <h2>EventDetails</h2>;
}

function Messages() {
  return <h2>Messages</h2>;
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/configuracion" element={<Configuracion />} />
        <Route path="/eventdetails" element={<EventDetails />} />
        <Route path="/messages" element={<Messages />} />
      </Routes>
    </Router>
  );
}

export default App;


