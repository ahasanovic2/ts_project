import React, {useState} from 'react';
import '../css/VotersPage.css';
import HowVoter from './HowVoter';
import WhoVoter from './WhoVoter';
import { useHistory, Switch, Route, useRouteMatch, useLocation } from 'react-router-dom';
import Header from './Header';

const VotersPage = () => {

    const match = useRouteMatch();
    const location = useLocation();
    const history = useHistory();
    const [showContactInfo, setShowContactInfo] = useState(false);

    const handleContactHover = () => {
        setShowContactInfo(true);
    };

    const handleContactLeave = () => {
        setShowContactInfo(false);
    };


    const handleSwitchToLanding = () => {
        history.push('/landing');
    };

    const handleSwitchToVoters = () => {
        history.push('/voters');
    };

    const handleSwitchToLegislativa = () => {
        history.push('/legislativa');
    };

    const handleSwitchToIzbori = () => {
        history.push('/election')
    };

    const handleSwitchToResults = () => {
        history.push('/results')
    };

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push('/');
    };

    const handleButtonClick = (route) => {
        history.push(`/voters/${route}`);
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
