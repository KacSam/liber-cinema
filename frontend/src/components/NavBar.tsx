import React from 'react';
import './NavBar.css';

const NavBar: React.FC = () => {
    return (
        <nav className = "nav-container">
            <div className="nav-bar">
                <button className="nav-button">Książki</button>
                <button className="nav-button">Serie</button>
                <button className="nav-button">Rankingi</button>
                <button className="nav-button">Moja biblioteka</button>
                <button className="nav-button">Zaloguj się</button>
            </div>
        </nav>

    );
};

export default NavBar;