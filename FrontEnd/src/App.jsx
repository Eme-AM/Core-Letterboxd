import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './views/Dashboard'; 
import Messages from './views/Messages/Messages';
import './globals.scss';
import Configuration from './views/Configuration/Configuration';

function EventDetails() {
  return <h2>EventDetails</h2>;
}
 
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/configuration" element={<Configuration />} />
        <Route path="/eventdetails" element={<EventDetails />} />
        <Route path="/messages" element={<Messages />} />
      </Routes>
    </Router>
  );
}

export default App;


