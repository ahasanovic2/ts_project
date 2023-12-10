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
        if (role === 'ROLE_SUPERADMIN') {
            history.push('/superadmin-home');
            window.location.reload();
            alert('You tried to access Admin\'s page while not logged in as admin. You have been returned to superadmin\'s homepage.');
        } else if (role === 'ROLE_USER') {
            history.push('/landing');
            window.location.reload();
            alert('You tried to access Admin\'s page while not logged in as admin. You have been returned to user\'s homepage.');
        }
    }, []);

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
                <button onClick={() => handleRefreshToken()}>Obnovi sesiju
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