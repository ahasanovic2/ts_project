import React, {useState, useEffect} from "react";
import '../css/CreatingLists.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";
import {checkExpiration, useHandleLogout} from "../../HelpFunctions";

const CreatingLists = () => {

    const history = useHistory();
    const [electionName, setElectionName] = useState("");
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [errorMessageDescription, setErrorMessageDescription] = useState("");
    const [elections, setElections] = useState([]);
    const handleLogout = useHandleLogout();

    const fetchElections = async () => {
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://localhost:8080';
        const token = localStorage.getItem('access_token');
        const headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append('Authorization', `Bearer ${token}`);

        const response = await fetch(`${BASE_URL}/elections`, { headers });
        const data = await response.json();
        setElections(data);
    };

    useEffect(() => {
        fetchElections();
    }, []);

    const handleSubmit = async (e) => {
        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            e.preventDefault();
            setErrorMessage("");
            setErrorMessageDescription("");

            if (description.length < 20) {
                setErrorMessageDescription("Description must be at least 20 characters long");
                return;
            }

            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://localhost:8080';
            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', `Bearer ${token}`);

            const body = JSON.stringify([{
                name,
                description
            }]);

            const response = await fetch(`${BASE_URL}/elections/election/add-lists?name=${electionName}`, {
                method: 'POST',
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
                alert('Successfully added list');
                setErrorMessage("");
                // Reset the input fields except for election name
                setName("");
                setDescription("");

            }
        }
    };

    const handleReset = () => {
        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            setElectionName("");
            setName("");
            setDescription("");
            setErrorMessage("");
            setErrorMessageDescription("");
        }
    };
    

    return (
        <div className="list-page">
            <Header />
            <div className="list-content">
                <form onSubmit={handleSubmit}>
                    <label>
                        Naziv izbora:
                        <select value={electionName} onChange={e => setElectionName(e.target.value)} required>
                        <option value="">--Molimo odaberite izbore--</option>
                            {elections.map(election => (
                                <option key={election.id} value={election.name}>{election.name}</option>
                            ))}
                        </select>
                    </label>
                    <label>
                        Naziv liste:
                        <input type="text" value={name} onChange={e => setName(e.target.value)} required/>
                    </label>
                    <label>
                        Opis:
                        <textarea value={description} onChange={e => setDescription(e.target.value)} required/>
                    </label>
                    {errorMessageDescription && <p className="error-class">{errorMessageDescription}</p>}
                    {errorMessage && <p className="error-class">{errorMessage}</p>}
                    <div className="dugmad">
                        <button type="button" id="clear-form" onClick={handleReset}>Očisti formu</button>
                        <button type="submit">Potvrdi</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreatingLists;