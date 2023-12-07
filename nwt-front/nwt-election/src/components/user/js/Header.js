// Header.js
import React, {useState} from 'react';
import { useHistory } from 'react-router-dom';

const Header = ({voting}) => {
    const history = useHistory();

    const handleSwitchTo = (path) => {
        if (voting === true) {
            localStorage.removeItem('electionName');
        }

        history.push(path);
    };

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push('/');
    };

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
                <button onClick={() => handleSwitchTo('/change-password')}>Promijeni lozinku
                    <br/>
                </button>
                <button onClick={handleLogout}>Odjava</button>
            </div>
        </div>
    );
};

export default Header;
