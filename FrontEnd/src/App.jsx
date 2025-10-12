import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './views/Dashboard';
import Messages from './views/Messages/Messages';
import './globals.scss';
import Configuration from './views/Configuration/Configuration';
import { ToastContainer } from 'react-toastify';

function EventDetails() {
  return <h2>EventDetails</h2>;
}

function App() {
  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/configuration" element={<Configuration />} />
          <Route path="/eventdetails" element={<EventDetails />} />
          <Route path="/messages" element={<Messages />} />
        </Routes>
      </Router>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        pauseOnHover 
      />
    </>
  );
}

export default App;


