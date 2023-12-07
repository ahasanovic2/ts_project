import React from 'react';
import '../css/LandingPage.css';
import './WhoVoter';
import Header from './Header';

const LandingPage = (props) => {
    
    return (
        <div className="landing-page">
            <Header/>
            <div className="user-content">
                <h2>Dobrodošli na online glasanje na izborima u BiH</h2>
                <p>
                    Online glasanje na izborima u Bosni i Hercegovini vam omogućava da udobno i sigurno glasate iz udobnosti svog doma.
                    Bez obzira gdje se nalazite, možete iskoristiti svoje biračko pravo i doprinijeti demokratskom procesu.
                </p>
                <p>
                    Naša platforma omogućava jednostavno registrovanje, sigurno provođenje izbora i transparentno prikazivanje rezultata.
                    Uz pomoć naše aplikacije, možete pristupiti informacijama o kandidatima, glasati za svoje favorite i pratiti izborne rezultate u realnom vremenu.
                </p>
                <p>
                    <b>Vaš glas je važan! Pridružite se online glasanju i izaberite budućnost Bosne i Hercegovine.</b>
                </p>
            </div>
        </div>
    );
};

export default LandingPage;
