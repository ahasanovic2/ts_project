import React, {useState} from 'react';
import '../css/HowVoter.css'
import { useHistory } from 'react-router-dom';
import Header from './Header';

const HowVoter = () => {

    return (
        <div className="landing-page">
            <Header />
            <div className="centrirano">
            <section>
                <h2>Kako glasati?</h2>
                <p>Da biste glasali na dan izbora:</p>
                <p>-morate biti registrovani na našoj aplikaciji,</p>
                <p>-prilikom registracije morate unijeti svoj JMBG i jedinstvenu email adresu.</p>
            </section>
            </div>
        </div>
    );
};

export default HowVoter;
