import React, { useState } from 'react';
import './NavBar.css';
import LoginModal from './LoginModal';

interface NavBarProps {
    onBooksClick: () => void;
}

const NavBar: React.FC<NavBarProps> = ({ onBooksClick }) => {
    const [showLoginModal, setShowLoginModal] = useState(false);

    return (
        <nav className="nav-container">
            <div className="nav-bar">
                <button className="nav-button" onClick={onBooksClick}>Książki</button>
                <button className="nav-button">Serie</button>
                <button className="nav-button">Rankingi</button>
                <button className="nav-button">Moja biblioteka</button>
                <button className="nav-button" onClick={() => setShowLoginModal(true)}>Zaloguj się</button>
            </div>
            
            {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} />}
        </nav>
    );
};

export default NavBar;