import React, { useState, useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import '../css/ChoosePollingStation.css';

function ChoosePSPage() {
    const history = useHistory();
    const [pollingStations, setPollingStations] = useState([]);
    const [selectedStation, setSelectedStation] = useState('');

    useEffect(() => {
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://44.218.241.227:8080';
        const token = localStorage.getItem('access_token');
    
        fetch(`${BASE_URL}/pollingStations`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then(response => response.json())
            .then(data => {
                setPollingStations(data);
                if(data[0]){
                   setSelectedStation(data[0].name); // set first polling station as default
                }
            })
            .catch(err => console.error(err));
    }, []);

    const handleSubmit = async () => {
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://44.218.241.227:8080';
        const token = localStorage.getItem('access_token');
        
        const response = await fetch(`${BASE_URL}/users/pollingStation?name=${selectedStation}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${token}`,
            },
        });

        if (response.ok) {
            history.push('/landing');
        } else {
            console.error('Error:', response.status, response.statusText);
        }
    };

    return (
        <div className="choosePS-page">
            <div className="frameChoosePS">
                <h2 className="font-choosePS">Izaberi izbornu stanicu</h2>
                <select 
                    value={selectedStation} 
                    onChange={e => setSelectedStation(e.target.value)}
                    className="dropdown"
                >
                    {pollingStations.map(station => 
                        <option key={station.id} value={station.name}>
                            {station.name}, {station.address}, {station.opcina}, {station.kanton}, {station.entitet}
                        </option>
                    )}
                </select>
                <button className="choosePS-button" onClick={handleSubmit}>
                    Potvrdi
                </button>
            </div>
        </div>
    );
}

export default ChoosePSPage;
