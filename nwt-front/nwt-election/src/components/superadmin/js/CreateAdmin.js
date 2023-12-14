import React, { useState, useEffect } from "react";
import axios from "axios";
import '../css/CreateAdmin.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";
import {checkExpiration,useHandleLogout} from "../../HelpFunctions";

const SACreateAdmin = () => {
    const history = useHistory();
    const [firstname, setFirstname] = useState("");
    const [lastname, setLastname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const handleLogout = useHandleLogout();

    const handleSubmit = async (e) => {
        checkExpiration(localStorage.getItem('access_token'), handleLogout);
        if (localStorage.getItem('access_token')) {
            e.preventDefault();
            setErrorMessage("");

            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://100.0.0.153:8080';
            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', `Bearer ${token}`);

            const body = JSON.stringify({
                firstname,
                lastname,
                email,
                password,
                "role": "ADMIN"
            });

            const response = await fetch(`${BASE_URL}/users/create-admin`, {
                method: 'POST',
                headers,
                body
            });

            if (!response.ok) {
                const errorData = await response.json();
                if(Array.isArray(errorData)) {
                    setErrorMessage(errorData[0].message);
                } else {
                    setErrorMessage(errorData.message);
                }
            } else {
                alert('Successfully added admin');
                setErrorMessage("");
                setFirstname("");
                setLastname("");
                setEmail("");
                setPassword("");
            }
        }
    };

    const handleReset = () => {
        checkExpiration(localStorage.getItem('access_token'), handleLogout);
        if (localStorage.getItem('access_token')) {
            setErrorMessage("");
            setFirstname("");
            setLastname("");
            setEmail("");
            setPassword("");
        }
    };

    return (
        <div className="create-admin">
            <Header />
            <div className="body-create-admin">
                <form onSubmit={handleSubmit}>
                    <label>
                        Ime:
                        <input type="text" value={firstname} onChange={e => setFirstname(e.target.value)} required/>
                    </label>
                    <label>
                        Prezime:
                        <input type="text" value={lastname} onChange={e => setLastname(e.target.value)} required/>
                    </label>
                    <label>
                        Email:
                        <input type="text" value={email} onChange={e => setEmail(e.target.value)} required/>
                    </label>
                    <label>
                        Sifra:
                        <input type="text" value={password} onChange={e => setPassword(e.target.value)} required/>
                    </label>
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

export default SACreateAdmin;