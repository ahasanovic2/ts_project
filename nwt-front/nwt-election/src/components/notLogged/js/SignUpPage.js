import React, { useState, useRef } from 'react';
import { useHistory } from 'react-router-dom';
import '../css/SignUpPage.css';
import axios from 'axios';

function SignUpPage() {
    const history = useHistory();
    const [selectedOption, setSelectedOption] = useState('');
    const inputNumber = useRef(null);
    const inputEmail = useRef(null);

    function validateInput(input) {
        const id = input.id;
        const value = input.value;
        const errorText = document.getElementById(`errorText-${id}`);

        if (id === 'email') {
            const emailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

            if (!value.match(emailRegex)) {
                errorText.style.display = 'block';
                inputEmail.current.classList.add('error-field');
                return false;
            } else {
                errorText.style.display = 'none';
                inputEmail.current.classList.remove('error-field');
                return true;
            }
        }
    }

    const handleSignUp = async () => {
        const isEmailValid = validateInput(inputEmail.current);

        if (isEmailValid) {
            const firstName = document.getElementById("firstName").value;
            const lastName = document.getElementById("lastName").value;
            const email = inputEmail.current.value;
            const password = document.getElementById("password").value;
            const role = "USER"; // since role is always "USER"

            const BASE_URL = process.env.REACT_APP_BASE_URL ||  'http://localhost:8080';
            const response = await fetch(`${BASE_URL}/authentication/register`, { // replace with your API url
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    firstname: firstName,
                    lastname: lastName,
                    email: email,
                    password: password,
                    role: role,
                }),
            });


            if (response.ok) {
                history.push('/verification');
            } else if(response.status === 409) { // if status is 409 Conflict
                // handle email already existing...
                const data = await response.json();
                console.error('Error:', data.message);
                alert(data.message);
            } else {
                console.error('Error:', response.status, response.statusText);
                // handle other errors...
            }



            /* if(response.ok) { // if HTTP status is 200-299
                // get the response data
                const data = await response.json();
                localStorage.setItem('access_token',data.access_token)
                localStorage.setItem('refresh_token',data.refresh_token)
                // Rest of the registration processing code
                history.push('/choose-pollingstation');
            } else if(response.status === 409) { // if status is 409 Conflict
                // handle email already existing...
                const data = await response.json();
                console.error('Error:', data.message);
                alert(data.message);
            } else {
                console.error('Error:', response.status, response.statusText);
                // handle other errors...
            } */
        }
    };

    return (
        <div className="signUp-page">
            <div className="frameSignUp">
        <span style={{ marginTop: '5px', marginBottom: '30px' }}>
          <h2 className="font-signUp">Registracija korisnika</h2>
        </span>
                <div className="input-container-signUp">
                    <input className={`input-field-signUp ${selectedOption === 'firstName' && 'error-field'}`} type="text" id="firstName" placeholder="Ime" />
                </div>
                <div className="input-container-signUp">
                    <input className={`input-field-signUp ${selectedOption === 'lastName' && 'error-field'}`} type="text" id="lastName" placeholder="Prezime" />
                </div>
                <div className="input-container-signUp">
                    <input className={`input-field-signUp ${selectedOption === 'email' && 'error-field'}`} type="email" id="email" placeholder="E-mail adresa" ref={inputEmail} />
                    <p id="errorText-email" style={{ color: 'red', display: 'none' }}>
                        Unesite validnu email adresu.
                    </p>
                </div>
                <div className="input-container-signUp">
                    <input className={`input-field-signUp ${selectedOption === 'password' && 'error-field'}`} type="password" id="password" placeholder="Lozinka" />
                </div>
                <button className="signup-button" onClick={handleSignUp}>
                    Registracija
                </button>
                <div className="links">
                    <button className="buttonLink" onClick={() => history.push('/login')}>Već si registrovan/a? <b>Prijavi se.</b></button>
                </div>
            </div>
        </div>
    );
}

export default SignUpPage;
