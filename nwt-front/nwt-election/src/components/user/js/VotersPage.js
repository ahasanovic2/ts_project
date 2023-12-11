import React from 'react';
import '../css/VotersPage.css';
import HowVoter from './HowVoter';
import WhoVoter from './WhoVoter';
import { useHistory, Switch, Route, useLocation } from 'react-router-dom';
import Header from './Header';
import {checkExpiration, useHandleLogout} from "../../HelpFunctions";

const VotersPage = () => {

    const location = useLocation();
    const history = useHistory();
    const handleLogout = useHandleLogout();

    const handleButtonClick = (route) => {
        checkExpiration(localStorage.getItem('access_token'),handleLogout);
        if (localStorage.getItem('access_token')) {
            history.push(`/voters/${route}`);
        }
    };

    return (
        <div className="landing-page">
            <Header/>
            <div className="left-sidebar">
                <button
                    className={`sidebar-button ${location.pathname === '/voters/whoVoter' ? 'active' : ''}`}
                    onClick={() => handleButtonClick('whoVoter')}
                >
                    Ko može glasati?
                </button>
                <button
                    className={`sidebar-button ${location.pathname === '/voters/howVoter' ? 'active' : ''}`}
                    onClick={() => handleButtonClick('howVoter')}
                >
                    Kako glasati?
                </button>
                <Switch>
                    <Route path='/voters/whoVoter' component={WhoVoter} />
                    <Route path='/voters/howVoter' component={HowVoter} />
                </Switch>
            </div>
        </div>
    );
};

export default VotersPage;
