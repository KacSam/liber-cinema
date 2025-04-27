import React from 'react';
import './NavBar.css';

interface NavBarProps {
    onBooksClick: () => void;
}

const NavBar: React.FC<NavBarProps> = ({ onBooksClick }) => {
    return (
        <nav className = "nav-container">
            <div className="nav-bar">
                <button className="nav-button" onClick={onBooksClick}>Książki</button>
                <button className="nav-button">Serie</button>
                <button className="nav-button">Rankingi</button>
                <button className="nav-button">Moja biblioteka</button>
                <button className="nav-button">Zaloguj się</button>
            </div>
        </nav>

    );
};

export default NavBar;