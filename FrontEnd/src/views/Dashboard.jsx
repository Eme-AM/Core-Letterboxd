import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <h1>Bienvenido a la Home</h1>
      <p>Esta es la página principal de tu aplicación.</p>
      <nav>
        <ul>
          <li><Link to="/configuracion">Configuracion</Link></li>
          <li><Link to="/eventdetails">EventDetails</Link></li>
          <li><Link to="/messages">Messages</Link></li>
        </ul>
      </nav>
    </div>
  );
}

export default Home;
