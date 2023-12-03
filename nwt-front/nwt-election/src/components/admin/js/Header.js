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
                <button onClick={() => handleSwitchTo('/admin-landing')}>Početna
                    <br/>
                    <span className="small-text">Početna stranica aplikacije</span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-create-elections')}>Kreiraj izbore
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-create-lists')}>Kreiraj liste
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-create-candidates')}>Kreiraj kandidate
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-create-pollingstations')}>Kreiraj izborne stanice
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-add-pollingstations')}>Dodijeli izborne stanice izborima
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-users')}>Pregledaj korisnike
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={() => handleSwitchTo('/admin-change-password')}>Promijeni lozinku
                    <br/>
                    <span className='small-text'></span>
                </button>
                <button onClick={handleLogout}>Odjava</button>
            </div>
        </div>
    );
};

export default Header;