import React, {useState} from 'react';
import '../css/WhoVoter.css';
import Header from './Header';

const WhoVoter = () => {
    return (
        <div className="landing-page">
            <Header/>
            <div className="centrirano">
                <section>
                    <h2>Ko može glasati?</h2>
                    <p>Da bi se registrovali i glasali na izborima morate:
                        <br/>
                        <p>-imati najmanje 18 godina,</p>
                        <p>-imati BH državljanstvo</p>
                    </p>
                </section>
            </div>
        </div>
    );
};

export default WhoVoter;
