import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
<<<<<<< HEAD
import Dashboard from './views/Dashboard';
import Messages from './views/Messages/Messages';
import './globals.scss';
import Configuration from './views/Configuration/Configuration';
import { ToastContainer } from 'react-toastify';
=======
import Dashboard from './views/Dashboard'; 
import Messages from './views/Messages/Messages';
import './globals.scss';

function Configuracion() {
  return <h2>Configuracion</h2>;
}
>>>>>>> ad7b2ab2b8095c5c11c6f416f4f89c50e3d202c9

function EventDetails() {
  return <h2>EventDetails</h2>;
}
<<<<<<< HEAD

=======
 
>>>>>>> ad7b2ab2b8095c5c11c6f416f4f89c50e3d202c9
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


