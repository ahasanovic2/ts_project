import React, {useState} from "react";
import '../css/CreatePollingStations.css';
import { useHistory } from "react-router-dom";
import Header from "./Header";

const SACreatePollingStations = () => {
    const history = useHistory();
    const [name, setName] = useState("");
    const [address, setAddress] = useState("");
    const [opcina, setOpcina] = useState("");
    const [kanton, setKanton] = useState("Unsko Sanski");
    const [entitet, setEntitet] = useState("FederacijaBiH");
    const [errorMessage, setErrorMessage] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage("");

        const token = localStorage.getItem('access_token');
        const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://44.218.241.227:8080';
        const headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append('Authorization', `Bearer ${token}`);
    
        const body = JSON.stringify({
            name,
            address,
            opcina,
            kanton: entitet === 'FederacijaBiH' ? kanton : undefined,
            entitet
        });
    
        const response = await fetch(`${BASE_URL}/pollingStations/create`, {
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
            alert('Successfully added polling station');
            setErrorMessage("");
            setName("");
            setAddress("");
            setOpcina("");
            setKanton("Unsko Sanski");
            setEntitet("FederacijaBiH");
        }
    };

    const handleReset = () => {
        setName("");
        setAddress("");
        setErrorMessage("");
        setOpcina("");
        setKanton("Unsko Sanski");
        setEntitet("FederacijaBiH");
    };

    return (
        <div className="polling-stations">
            <Header />
            <div className="pollingstation-content">
                <form onSubmit={handleSubmit}>
                    <label>
                        Naziv:
                        <input type="text" value={name} onChange={e => setName(e.target.value)} required/>
                    </label>
                    <label>
                        Adresa:
                        <input type="text" value={address} onChange={e => setAddress(e.target.value)} required/>
                    </label>
                    <label>
                        Opcina:
                        <input type="text" value={opcina} onChange={e => setOpcina(e.target.value)} required/>
                    </label>
                    <label>
                        Entitet:
                        <select value={entitet} onChange={e => setEntitet(e.target.value)}>
                            <option value="FederacijaBiH">FederacijaBiH</option>
                            <option value="RepublikaSrpska">RepublikaSrpska</option>
                        </select>
                    </label>
                    {entitet === 'FederacijaBiH' &&
                    <label>
                        Kanton:
                        <select value={kanton} onChange={e => setKanton(e.target.value)}>
                            <option value="Unsko Sanski">Unsko Sanski</option>
                            <option value="Posavski">Posavski</option>
                            <option value="Tuzlanski">Tuzlanski</option>
                            <option value="Zenicko Dobojski">Zenicko Dobojski</option>
                            <option value="Bosansko Podrinjski">Bosansko Podrinjski</option>
                            <option value="Srednjobosanski">Srednjobosanski</option>
                            <option value="Hercegovacko neretvanski">Hercegovacko neretvanski</option>
                            <option value="Zapadnohercegovacki">Zapadnohercegovacki</option>
                            <option value="Sarajevo">Sarajevo</option>
                            <option value="Kanton 10">Kanton 10</option>
                        </select>
                    </label>}
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

export default SACreatePollingStations;