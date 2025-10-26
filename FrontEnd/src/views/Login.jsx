import React, { useState } from 'react';
import './Login.scss';
import appLogo from '../assets/App Logo.png';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        // Lógica de login
        console.log('Login:', { username, password });
    };

    return (
        <div className="login-container">
            <div className="login-card">
                <div className="login-header">
                    <img src={appLogo} alt="cineTrack Logo" className="login-logo" />
                    <h1 className="login-title">cineTrack</h1>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="form-group">
                        <input
                            type="text"
                            placeholder="Usuario"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="form-input"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <input
                            type="password"
                            placeholder="Contraseña"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="form-input"
                            required
                        />
                    </div>

                    <a href="#" className="forgot-password">Olvidé mi contraseña</a>

                    <button type="submit" className="login-button">
                        INICIAR SESIÓN
                    </button>
                </form>

                <div className="register-section">
                    <p className="register-text">¿No tienes una cuenta?</p>
                    <a href="#" className="register-link">Regístrate ya!</a>
                </div>
            </div>
        </div>
    );
}

export default Login;
