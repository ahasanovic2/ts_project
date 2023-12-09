import React, {useEffect, useState} from 'react';
import "../css/ElectionPage.css"
import axios from 'axios';
import { useHistory } from 'react-router-dom';
import { ElectionContext } from './ElectionContext';
import moment from 'moment/moment';
import Header from './Header';

const ElectionPage = () => {
    const [elections, setElections] = useState([]);

    const [selectedElection, setSelectedElection] = useState(null);

    useEffect(() => {
        const fetchElections = async () => {
            const token = localStorage.getItem('access_token');
            try {
                const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://44.218.241.227:8080';
                const response = await axios.get(`${BASE_URL}/voting/elections`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setElections(response.data);
            } catch (error) {
                console.error('Failed to fetch elections:', error);
            }
        };

        fetchElections();
    }, []);

    const handleElectionClick = (electionId) => {
        // Ovdje možete implementirati logiku za prikaz dodatnih informacija o izboru
        // i omogućiti glasanje ako je status aktivan
        console.log(`Kliknut izbor s ID-om: ${electionId}`);
    };

    const history = useHistory();

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push('/');
    };
    
    const handleSwitchToVotingPage = (election) => {
        localStorage.setItem('electionName',election.name);
        console.log("Election name is ", localStorage.getItem('electionName'));
        history.push('/voting-page');
    };

    return (
        <ElectionContext.Provider value={selectedElection}>
        <div>
            <Header />        
            <div className='container'>
                <div className="rezultati">
                    <p>
                        Na sljedećem linku možete vidjeti rezultate izbora 2022 godine:
                    </p>
                    <a href="https://www.izbori.ba/Rezultati_izbora/?resId=32&langId=1" target="_blank" rel="noopener noreferrer">
                        Rezultati 2022.
                    </a>
                </div>
                <h1>IZBOR U BOSNI I HERCEGOVINI</h1>
                <div className="election-list">
                    {elections.map(election => (
                        <div className="election-card" key={election.id} onClick={() => handleElectionClick(election.id)}>
                            <h2 className="title">{election.name}</h2>
                            <p>{election.description}</p>
                            <p>Vrijeme početka: {moment(election.startTime).format('MMMM Do YYYY, h:mm:ss a')}</p>
                            <p>Vrijeme kraja: {moment(election.endTime).format('MMMM Do YYYY, h:mm:ss a')}</p>
                            <p>Status: <span style={{fontWeight: 'bold', color: election.status === 'Active' ? 'green' : (election.status === 'Finished' ? 'red' : 'orange')}}>{election.status}</span></p>
                            {election.status === 'Active' && (
                                <button onClick={(event) => {
                                    event.stopPropagation();
                                    handleSwitchToVotingPage(election);
                                }}>                     
                            Glasaj
                            </button>
                        )}
                    </div>
                ))}
                </div>
            </div>
        </div>
        </ElectionContext.Provider>
    );
};

export default ElectionPage;