import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import '../css/NotVerifiedPage.css';

function NotVerifiedPage() {

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

    const handleResend = async () => {

    };


    return (
        <div className='back'>
            <div className='frame'>
                <h1>
                    Da biste se prijavili na sistem morate verificirati mail!
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
                    <div className='resendButton'>
                        <button>
                            Resend
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default NotVerifiedPage;