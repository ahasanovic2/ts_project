import React, { useState, useEffect, useContext } from 'react';
import '../css/VotingPage.css'
import loginImage from "../../../images/login5.png";
import { useHistory } from 'react-router-dom';
import { ElectionContext } from './ElectionContext';
import axios from 'axios';
import Header from './Header';
import {checkExpiration, useHandleLogout} from "../../HelpFunctions";

function Candidate({ candidate, selectedVote, setSelectedVote }) {
    const handleClick = () => {
        setSelectedVote({ type: 'candidate', data: candidate });
    };

    const isSelected = selectedVote && selectedVote.type === 'candidate' && selectedVote.data.id === candidate.id;

    return (
        <div className={`candidate ${isSelected ? 'selected' : ''}`} onClick={handleClick}>
            <div className="image-containerLogin">
                <img src={loginImage} alt="Login" />
            </div>
            <h2>{candidate.firstName}</h2>
            <h2>{candidate.lastName}</h2>
            <p>{candidate.description}</p>
        </div>
    );
}

function List({ list, selectedVote, setSelectedVote }) {
    const handleClick = () => {
        setSelectedVote({ type: 'list', data: list });
    };

    const isSelected = selectedVote && selectedVote.type === 'list' && selectedVote.data.id === list.id;

    return (
        <div className={`candidate ${isSelected ? 'selected' : ''}`} onClick={handleClick}>
            <div className="image-containerLogin">
                <img src={loginImage} alt="Login" />
            </div>
            <h2>{list.name}</h2>
            <p>{list.description}</p>
        </div>
    );
}

function VotingPage({ candidates, lists, selectedVote, setSelectedVote, clearSelection }) {
    const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
    const token = localStorage.getItem('access_token');
    const history = useHistory();
    const handleLogout = useHandleLogout();

    const handleSubmit = async () => {

        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            if (!selectedVote) {
                alert('You need to select a candidate or a list before voting.');
                return;
            }

            try {
                if (selectedVote.type === 'candidate') {
                    const { firstName, lastName } = selectedVote.data;
                    const url = `${BASE_URL}/voting/vote-for-candidate?electionName=${localStorage.getItem('electionName')}&firstName=${firstName}&lastName=${lastName}`;
                    await axios.post(url, {}, { headers: { Authorization: `Bearer ${token}` } });
                    alert('Your vote for candidate has been casted!');
                } else if (selectedVote.type === 'list') {
                    const { name } = selectedVote.data;
                    const url = `${BASE_URL}/voting/vote-for-list?electionName=${localStorage.getItem('electionName')}&name=${name}`;
                    await axios.post(url, {}, { headers: { Authorization: `Bearer ${token}` } });
                    alert('Your vote for list has been casted!');
                }

                clearSelection();  // Reset selection
                history.push(`/landing`);
            } catch (error) {
                console.error('Failed to cast vote:', error);
                alert('Failed to cast vote. Please try again.');
            }
        }
    };

    return (
        <div>
            <div className="VotingPage">
                <h1>Voting in elections</h1>
                <div className="candidates">
                    {candidates.map(candidate => (
                        <Candidate
                            key={candidate.id}
                            candidate={candidate}
                            selectedVote={selectedVote}
                            setSelectedVote={setSelectedVote}
                        />
                    ))}
                </div>
                <div className="lists">
                    {lists.map(list => (
                        <List
                            key={list.id}
                            list={list}
                            selectedVote={selectedVote}
                            setSelectedVote={setSelectedVote}
                        />
                    ))}
                </div>
                <button onClick={clearSelection}>Clear selection</button>
                <button onClick={handleSubmit}>Confirm vote</button>
            </div>
        </div>
    );
}

function VotingPageFinal() {
    const [candidates, setCandidates] = useState([]);  // Add state for candidates
    const [lists, setLists] = useState([]);
    const [selectedVote, setSelectedVote] = useState(null);
    const selectedElection = useContext(ElectionContext); // Access the selected election from context

    const clearSelection = () => {
        setSelectedVote([]);
    };

    const history = useHistory();

    useEffect(() => {
        const fetchCandidates = async () => {
            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
            try {
                const response = await axios.get(`${BASE_URL}/elections/election/candidates?name=${localStorage.getItem('electionName')}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setCandidates(response.data);
            } catch (error) {
                console.error('Failed to fetch candidates:', error);
            }
        };

        const fetchLists = async () => {
            const token = localStorage.getItem('access_token');
            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://10.0.0.155:8080';
            try {
                const response = await axios.get(`${BASE_URL}/elections/election/lists?name=${localStorage.getItem('electionName')}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setLists(response.data);
            } catch (error) {
                console.error('Failed to fetch lists:', error);
            }
        }

        if (localStorage.getItem('electionName')) {
            fetchCandidates();
            fetchLists();
        }
    }, [selectedElection]);

    return (
        <div>
            <Header/>
            <div className="App">
                <h1>Information about candidates and lists</h1>
                <VotingPage
                    candidates={candidates} // Pass fetched candidates to the VotingPage
                    lists={lists} // Pass fetched lists to the VotingPage
                    selectedVote={selectedVote}
                    setSelectedVote={setSelectedVote}
                    clearSelection={clearSelection}
                />
            </div>
        </div>
    );
}

export default VotingPageFinal;