// Header.js
import React, {useEffect, useState} from 'react';
import { useHistory } from 'react-router-dom';
import GetRole from "../../GetRole";
import { useHandleLogout, checkExpiration, RefreshToken } from "../../HelpFunctions";

const Header = ({voting}) => {
    const history = useHistory();
    const handleLogout = useHandleLogout();

    const handleSwitchTo = (path) => {
        checkExpiration(localStorage.getItem('access_token'), handleLogout);
        // Check if the token is still in the local storage
        if (localStorage.getItem('access_token')) {
            history.push(path);
        }
    };

    const handleRefreshToken = async () => {
        await RefreshToken();
    };

    useEffect(() => {
        var role = GetRole(localStorage.getItem('access_token'));
        if (role === 'ROLE_ADMIN') {
            history.push('/admin-landing');
            window.location.reload();
            alert('You tried to access User\'s page while not logged in as admin. You have been returned to admin\'s homepage.');
        } else if (role === 'ROLE_SUPERADMIN') {
            history.push('/superadmin-home');
            window.location.reload();
            alert('You tried to access User\'s page while not logged in as admin. You have been returned to superadmin\'s homepage.');
        }
    }, []);

    const [showContactInfo, setShowContactInfo] = useState(false);

    const handleContactHover = () => {
        setShowContactInfo(true);
    };

    const handleContactLeave = () => {
        setShowContactInfo(false);
    };

    return (
        <div className="header">
            <h1>E-izbori</h1>
            <div className="nav-buttons">
                <button onClick={() => handleSwitchTo('/landing')}>Početna
                    <br/>
                    <span className="small-text">Početna stranica aplikacije</span>
                </button>
                <button onClick={() => handleSwitchTo('/voters')}>Glasači
                    <br/>
                    <span className="small-text">Sve što glasač treba da zna</span>
                </button>
                <button onClick={() => handleSwitchTo('/election')}>Izbori
                    <br/>
                    <span className="small-text">Izbori 2024</span>
                    <br/>
                    <span className="small-text">Rezultati 2022</span>
                </button>
                <button onClick={() => handleSwitchTo('/results')}>Rezultati
                    <br/>
                    <span className='small-text'>Rezultati 2024</span>
                </button>
                <button onClick={() => handleSwitchTo('/legislativa')}>Legislativa
                    <br/>
                    <span className="small-text">Zakon o provođenju izbora</span>
                </button>
                <button
                    onMouseEnter={handleContactHover}
                    onMouseLeave={handleContactLeave}
                >
                    Kontakt
                    <br/>
                    <span className="small-text">Kontaktirajte korisničku podršku ukoliko imate bilo kakvih pitanja</span>
                </button>
                <div className="contact-info">
                    <p>Broj telefona: 123-456-789</p>
                    <p>Email: info@eizbori.com</p>
                </div>
                <button onClick={() => handleRefreshToken()}>Obnovi sesiju
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/change-password')}>Promijeni lozinku
                    <br/>
                </button>
                <button onClick={handleLogout}>Odjava</button>
            </div>
        </div>
    );
};

export default Header;
