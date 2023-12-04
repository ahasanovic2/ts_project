import React, { useState } from 'react';
import loginImage from '../../../images/login5.png';

import {FiEye, FiEyeOff} from 'react-icons/fi';
import '../css/LoginPage.css'
import axios from 'axios';
import jwtDecode from 'jwt-decode';
import { useHistory } from 'react-router-dom';

function LoginPage(props) {

    const [showPassword, setShowPassword] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const history = useHistory();

    const togglePasswordVisibility = () => {
        setShowPassword(!showPassword);
    };

    const handleLogin = async () => {
        const BASE_URL = process.env.REACT_APP_BASE_URL || 'https://localhost:8443';
        
        try {
            const response = await axios.post(`${BASE_URL}/authentication/login`, {email, password});
            const tokens = response.data;
            localStorage.setItem('access_token', tokens.access_token);
            localStorage.setItem('refresh_token', tokens.refresh_token);

            const decodedToken = jwtDecode(tokens.access_token);

            // Check for polling station
            try {
                if (decodedToken.role === 'ROLE_ADMIN') {
                    history.push('/admin-landing');
                    return;
                } else if (decodedToken.role === 'ROLE_SUPERADMIN') {
                    history.push('/superadmin-home');
                    return;
                } 
                const response2 = await axios.get(`${BASE_URL}/pollingStations/user`, {
                    headers: { 
                        'Authorization': `Bearer ${tokens.access_token}` 
                    }
                });

                if (decodedToken.role === 'ROLE_USER') {
                    history.push('/landing');
                }
            } catch (pollingError) {
                if (pollingError.response && pollingError.response.status === 404) {
                    // Polling station not found, redirect to polling station choice page
                    history.push('/choose-pollingstation');
                } else {
                    // Handle other errors
                    setError("Error checking polling station. Please try again later.");
                }
            }
        } catch (error) {
            if (error.response && error.response.status  === 404) {
                setError(error.response.data.message);
            } else if (error.response && error.response.status === 400) {
                localStorage.setItem('email', email);
                history.push('/not-verified');
            } 
            else {
                setError("Something went wrong. Please try again later.");
            }
        }
    };
    const message = props.location?.state?.message || null;
    return (
        <div className="login-page">
            <div className="frameLogIn">
                {message && <div className='missing_login'>{message}</div>}
                <h2 className="fontLogin">Prijava korisnika</h2>
                <div className="input-containerLogin">
                    <img src={loginImage} alt="Login"/>
                </div>
                <div className="input-containerLogin">
                    <input
                        className="input-field-emailLogin"
                        type="email"
                        id="email"
                        placeholder="E-mail adresa"
                        onChange={(e) => setEmail(e.target.value)}
                        value={email}
                    />
                </div>
                <div className="input-containerLogin">
                    <input 
                        className="input-fieldLogin" 
                        type={showPassword ? 'text' : 'password'} 
                        id="password" 
                        placeholder="Lozinka" 
                        onChange={(e) => setPassword(e.target.value)}
                        value={password}
                    />
                    <span style={{ marginTop: '4px', marginBottom: '-15px', fontSize: '1.5em'  }}>
                    {showPassword ? (
                        <FiEyeOff onClick={togglePasswordVisibility} />
                    ) : (
                        <FiEye onClick={togglePasswordVisibility} />
                    )}
                    </span>
                </div>
                <button className="login-button" onClick={handleLogin}>Prijava</button>
                <div>
                    {error && <p className="error-message">{error}</p>}
                </div>
                <div className="links">
                    <a className="link" href="#">Izgubljena lozinka?</a>
                    <button className="buttonLink" onClick={()=> history.push('/sign-up')}>Nemaš nalog? <b>Registruj se.</b></button>
                </div>
            </div>
        </div>
    );

}

export default LoginPage;