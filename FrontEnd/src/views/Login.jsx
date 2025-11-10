import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import { useAuth } from '../contexts/AuthContext';
import './Login.scss';
import appLogo from '../assets/App Logo.png';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const formData = new URLSearchParams();
            formData.append('grant_type', 'password');
            formData.append('username', username);
            formData.append('password', password);
            formData.append('scope', '');
            formData.append('client_id', '');
            formData.append('client_secret', '');

            const response = await fetch(
                'https://usuariosbe.cine-track.com.ar/api/v1/auth/login',
                {
                    method: 'POST',
                    headers: {
                        'accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData.toString(),
                }
            );

            if (!response.ok) {
                throw new Error('Credenciales inválidas');
            }

            const data = await response.json();
            const decodedToken = jwtDecode(data.access_token);
            const userRole = decodedToken.role || decodedToken.user_role || decodedToken.roles;

            if (userRole !== 'admin') {
                throw new Error('Acceso denegado. Solo los administradores pueden acceder.');
            }

            if (data.refresh_token) {
                localStorage.setItem('refresh_token', data.refresh_token);
            }

            login(decodedToken, data.access_token);
            navigate('/');
        } catch (error) {
            console.error('Error al iniciar sesión:', error);
            setError(error.message || 'Error al iniciar sesión');
        } finally {
            setLoading(false);
        }
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
                            disabled={loading}
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
                            disabled={loading}
                        />
                    </div>

                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? 'INICIANDO SESIÓN...' : 'INICIAR SESIÓN'}
                    </button>

                    {error && <p className="error-message">{error}</p>}
                </form>
            </div>
        </div>
    );
}

export default Login;
