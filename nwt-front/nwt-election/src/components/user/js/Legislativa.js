import React from 'react';
import '../css/Legislativa.css';
import Header from './Header';

const Legislativa = (props) => {

    return (
        <div className="landing-page">
            <Header/>
            <div className="contentLegislativa">
                <a href="https://www.izbori.ba/Documents/documents/ZAKONI/Tehnicki_precisceni_tekst/Tehnicki_precisceni_tekst_IZ_BiH_11_2022-bos.pdf" target="_blank" rel="noopener noreferrer">
                    Izborni zakon Bosne i Hercegovine
                </a>
                <br/>
                <br/>
                <a href="https://www.izbori.ba/Documents/documents/ZAKONI/IzborniZakonRS-18032013-1.PDF" target="_blank" rel="noopener noreferrer">
                    Izborni zakon Republike Srpske
                </a>
                <br/>
                <br/>
                <a href="https://www.izbori.ba/Documents/documents/ZAKONI/IZ_BRCKO_DISTRIKT-BOS.PDF" target="_blank" rel="noopener noreferrer">
                    Izborni zakon Brčko Distrikta BiH
                </a>
            </div>
        </div>
    );
};

export default Legislativa;
