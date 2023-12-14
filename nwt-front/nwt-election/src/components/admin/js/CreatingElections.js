import React, {useState} from "react";
import '../css/CreatingElections.css';
import { useHistory } from "react-router-dom";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import Header from "./Header";
import {checkExpiration, useHandleLogout} from "../../HelpFunctions";


const CreatingElections = () => {
    const history = useHistory();
    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [startTime, setStartTime] = useState(new Date());
    const [endTime, setEndTime] = useState(new Date());
    const [errorMessageTime, setErrorMessageTime] = useState("");
    const [errorMessageDescription, setErrorMessageDescription] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const handleLogout = useHandleLogout();

    const handleSubmit = async (e) => {
        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            e.preventDefault();
            setErrorMessageDescription("");
            setErrorMessageTime("");

            if (description.length < 20) {
                setErrorMessageDescription("Description must be at least 20 characters long");
                return;
            }
            if (startTime >= endTime) {
                setErrorMessageTime("Start time cannot be after end time");
                return;
            }

            setErrorMessageDescription("");
            setErrorMessageTime("");

            // Prepare request
            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://100.0.0.153:8080';
            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', `Bearer ${token}`);

            const body = JSON.stringify({
                name,
                description,
                startTime: startTime.toISOString(),
                endTime: endTime.toISOString()
            });

            // Send request
            const response = await fetch(`${BASE_URL}/elections/create`, {
                method: 'POST',
                headers,
                body
            });

            // Handle response
            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData[0].message);
                alert('Something went wrong');
            } else {
                alert('Successfuly added elections');
                setErrorMessage(""); // Clear the error message upon successful request
                history.push('/admin-landing');
            }
        }
    };

    return (
        <div className="creating-elections">
            <Header />
            <div className="content">
                <form onSubmit={handleSubmit}>
                    <label>
                        Naziv:
                        <input type="text" value={name} onChange={e => setName(e.target.value)} required/>
                    </label>
                    <br/>
                    <label>
                        Opis:
                        <textarea value={description} onChange={e => setDescription(e.target.value)} required minLength={20}/>
                    </label>
                    {errorMessageDescription && <p className="error-class">{errorMessageDescription}</p>}
                    <br/>
                    <label>
                        Početak izbora:
                        <DatePicker selected={startTime} onChange={date => setStartTime(date)} showTimeSelect dateFormat="Pp" minDate={new Date()} />
                    </label>
                    <label>
                        Kraj izbora:
                        <DatePicker selected={endTime} onChange={date => setEndTime(date)} showTimeSelect dateFormat="Pp" minDate={startTime} />
                    </label>
                    {errorMessageTime && <p className="error-class">{errorMessageTime}</p>}
                    <button type="submit">Potvrdi</button>
                    {errorMessage && <p className="error-class">{errorMessage}</p>}
                </form>
            </div>
        </div>
    );
};
export default CreatingElections;