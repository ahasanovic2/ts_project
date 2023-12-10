import React, {useEffect} from 'react';
import { useHistory } from 'react-router-dom';
import GetRole from "../../GetRole";
import { useHandleLogout, checkExpiration, RefreshToken } from "../../HelpFunctions";

const Header = () => {
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
            alert('You tried to access Superadmin\'s page while not logged in as admin. You have been returned to admin\'s homepage.');
        } else if (role === 'ROLE_USER') {
            history.push('/landing');
            window.location.reload();
            alert('You tried to access Superadmin\'s page while not logged in as admin. You have been returned to user\'s homepage.');
        }
    }, []);

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
                <button onClick={() => handleRefreshToken()}>Obnovi sesiju
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