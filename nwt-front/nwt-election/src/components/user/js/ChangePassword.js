import React, {useState} from "react";
import '../css/ChangePassword.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";

const ChangePassword = () => {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = async (e) => {
        try {
            e.preventDefault();
            setErrorMessage("");
        
            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://44.218.241.227:8080';
            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', `Bearer ${token}`);

            const body = JSON.stringify({
                oldPassword,
                newPassword
            });

            
        
            const response = await fetch(`${BASE_URL}/users/change-password`, {
                method: "PUT",
                headers,
                body
            });
        
            if (!response.ok) {
                const errorData = await response.json();
                // Check if errorData is an array or an object
                if(Array.isArray(errorData)) {
                    // Handle array of errors
                    setErrorMessage(errorData[0].message);
                } else {
                    // Handle single error object
                    setErrorMessage(errorData.message);
                }
            } else {
                alert('Successfully changed password');
                setOldPassword("");
                setNewPassword("");
                setErrorMessage("");
            }
        }
        catch (error) {
            if(Array.isArray(error)) {
                // Handle array of errors
                setErrorMessage(error[0].message);
            } else {
                // Handle single error object
                setErrorMessage(error.message);
            }
        }
    };

    const handleReset = () => {
        setOldPassword("");
        setNewPassword("");
        setErrorMessage("");
    };

    
    return(
        <div className="candidates">
            <Header />
            <div className="candidates-content">
                <form onSubmit={handleSubmit}>
                    <input 
                            className="input-password" 
                            type={showPassword ? 'text' : 'password'} 
                            id="old-password" 
                            placeholder="Stara sifra" 
                            onChange={(e) => setOldPassword(e.target.value)}
                            value={oldPassword}
                        />
                    <input 
                            className="input-password" 
                            type={showPassword ? 'text' : 'password'} 
                            id="new-password" 
                            placeholder="Nova sifra" 
                            onChange={(e) => setNewPassword(e.target.value)}
                            value={newPassword}
                        />
                    {errorMessage && <p className="error-class">{errorMessage}</p>}
                    <div className="dugmad">
                        <button type="button" id="clear-form" onClick={handleReset}>Očisti formu</button>
                        <button type="submit">Potvrdi</button>
                    </div>
                </form>
            </div>
        </div>
    );

};

export default ChangePassword;