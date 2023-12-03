import React from 'react';
import { useHistory } from 'react-router-dom';

const Header = () => {
    const history = useHistory();

    const handleSwitchTo = (path) => {
        history.push(path);
    };

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
        history.push('/');
    };

    return (
        <div className="header">
            <h1>E-izbori</h1>
            <div className="nav-buttons">
                <button onClick={() => handleSwitchTo('/superadmin-home')}>Početna
                    <br/>
                    <span className="small-text">Početna stranica aplikacije</span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-create-elections')}>Kreiraj izbore
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-create-lists')}>Kreiraj liste
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-create-candidates')}>Kreiraj kandidate
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-create-pollingstations')}>Kreiraj izborne stanice
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-add-pollingstations')}>Dodijeli izborne stanice izborima
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-results')}>Rezultati 2024
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/create-admin')}>Kreiraj administratora
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admins')}>Pregledaj administratore
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-users')}>Pregledaj korisnike
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/superadmin-change-password')}>Promijeni lozinku
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={handleLogout}>Odjava</button>
            </div>
        </div>
    );
};

export default Header;