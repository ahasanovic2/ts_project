import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import '../css/VerificationPage.css';

function VerificationPage() {

    const history = useHistory();

    useEffect(() => {
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');
    }, []);

    const handleLogin = async () => {
        history.push('/login');
    };

    const handleHome = async () => {
        history.push('/home');
    };


    return (
        <div className='back'>
            <div className='frame'>
                <h1>
                    Mail za verifikaciju vam je poslan putem mail-a! Provjerite vaš mail!
                </h1>
                <div className='buttons'>
                    <div className='loginButton'>
                        <button onClick={handleLogin}>
                            Login
                        </button>
                    </div>
                    <div className='homeButton'>
                        <button onClick={handleHome}>
                            Home
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default VerificationPage;