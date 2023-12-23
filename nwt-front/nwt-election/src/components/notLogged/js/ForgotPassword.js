import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import axios from 'axios';

const ForgotPassword = () => {
    const history = useHistory();
    const [email, setEmail] = useState("");

    const handleEmailChange = (event) => {
        setEmail(event.target.value);
    };
    const handleLoginRedirect = () => {
        history.push('/login');
    };

    const handleSubmit = async (event) => {
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
        event.preventDefault();
        
        try {
            const response = await axios.post(`${BASE_URL}/authentication/forgot-password?email=${email}`, {
                email: email
            });

            if (response.status==200) {
                console.log('Reset password email sent successfully');
                alert('Reset password email sent successfully');
            } else {
                console.error('Failed to send reset password email');
            }
        } catch (error) {
            console.error('Error sending reset password email:', error);
        }
    };

    return (
        <div className="photo-container-Homepage">
            <div className="container-Homepage">
               
                {/* Forgot Password Form */}
                <form className="forgot-password-form" onSubmit={handleSubmit}>
                    <label htmlFor="email">Unesite svoj email:</label>
                    <input
                        type="email"
                        id="email"
                        name="email"
                        value={email}
                        onChange={handleEmailChange}
                        required
                    />
                    <br/>
                    <br/>
                    <br/>
                    <button type="submit">Posalji email za resetovanje sifre</button>
                    <button onClick={handleLoginRedirect}>Nazad na prijavu</button>
                </form>
            </div>
        </div>
    );
};

export default ForgotPassword;
