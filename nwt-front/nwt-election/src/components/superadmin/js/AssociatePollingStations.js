import React, {useState, useEffect} from "react";
import '../css/AssociatePollingStations.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";
import {checkExpiration,useHandleLogout} from "../../HelpFunctions";

const SAAssociatePollingStations = () => {
    const history = useHistory();
    const [elections, setElections] = useState([]);
    const [pollingStations, setPollingStations] = useState([]);
    const [selectedElection, setSelectedElection] = useState("");
    const [selectedPollingStations, setSelectedPollingStations] = useState([]);
    const [errorMessage, setErrorMessage] = useState("");
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
    
    const fetchPollingStations = async () => {
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://localhost:8080';
        const token = localStorage.getItem('access_token');
        const headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append('Authorization', `Bearer ${token}`);

        const response = await fetch(`${BASE_URL}/pollingStations`, { headers });
        const data = await response.json();
        setPollingStations(data);
    };
    
    useEffect(() => {
        fetchElections();
        fetchPollingStations();
    }, []);

    const handlePollingStationChange = (e) => {
        checkExpiration(localStorage.getItem('access_token'), handleLogout);
        if (localStorage.getItem('access_token')) {
            if(e.target.checked) {
                setSelectedPollingStations([...selectedPollingStations, Number(e.target.value)]);
            } else {
                setSelectedPollingStations(selectedPollingStations.filter(ps => ps !== Number(e.target.value)));
            }
        }
    };
    
    const handleSubmit = async (e) => {
        checkExpiration(localStorage.getItem('access_token'), handleLogout);
        if (localStorage.getItem('access_token')) {
            e.preventDefault();
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://localhost:8080';
            const token = localStorage.getItem('access_token');
            const headers = new Headers();
            headers.append('Content-Type', 'application/json');
            headers.append('Authorization', `Bearer ${token}`);

            const body = JSON.stringify(selectedPollingStations);
            console.log(body);
            const response = await fetch(`${BASE_URL}/elections/election/set-pollingstations?name=${selectedElection}`, {
                method: 'POST',
                headers,
                body
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message);
            } else {
                alert('Successfully added polling stations to election');
                setErrorMessage("");
                setSelectedElection("");
                setSelectedPollingStations([]);
            }
        }
    };

    return (
        <div className="polling-stations-2">
            <Header />
            <div className="content-ps">
            <form onSubmit={handleSubmit}>
                <label>
                    Odaberi izbore:
                    <select value={selectedElection} onChange={(e) => setSelectedElection(e.target.value)}>
                    <option value="">--Molimo odaberite izbore--</option>
                    {elections.map(election => (
                        <option key={election.id} value={election.name}>{election.name}</option>
                    ))}
                    </select>
                </label>

                <table>
                    <thead>
                    <tr>
                        <th>Odaberi</th>
                        <th>Naziv</th>
                        <th>Adresa</th>
                        <th>Opcina</th>
                        <th>Kanton</th>
                        <th>Entitet</th>
                    </tr>
                    </thead>
                    <tbody>
                    {pollingStations.map(ps => (
                        <tr key={ps.id}>
                        <td>
                        <input 
                            type="checkbox" 
                            id={ps.name} 
                            name={ps.name} 
                            value={ps.id} 
                            checked={selectedPollingStations.includes(Number(ps.id))}
                            onChange={handlePollingStationChange} 
                        />
                        </td>
                        <td>{ps.name}</td>
                        <td>{ps.address}</td>
                        <td>{ps.opcina}</td>
                        <td>{ps.kanton ? ps.kanton : "-"}</td>
                        <td>{ps.entitet}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                <input className="dugme" type="submit" value="Submit" />
                </form>
                {errorMessage && <p>{errorMessage}</p>}
            </div>
        </div>
    );
};

export default SAAssociatePollingStations;