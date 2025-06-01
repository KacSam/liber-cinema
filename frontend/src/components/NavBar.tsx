import React, { useState, useEffect } from 'react';
import './NavBar.css';
import LoginModal from './LoginModal';
import SearchBar from './SearchBar';

interface NavBarProps {
    onMoviesClick: () => void;
    onLibraryClick: () => void; // Dodajemy nową właściwość
}

const NavBar: React.FC<NavBarProps> = ({ onMoviesClick, onLibraryClick }) => {
    const [showLoginModal, setShowLoginModal] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [username, setUsername] = useState('');
    const [showSearchBar, setShowSearchBar] = useState(false);

    useEffect(() => {
        // Sprawdzanie czy użytkownik jest zalogowany przy ładowaniu komponentu
        const token = localStorage.getItem('token');
        const user = localStorage.getItem('user');
        
        if (token && user) {
            setIsLoggedIn(true);
            const userData = JSON.parse(user);
            setUsername(userData.username);
        }
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setIsLoggedIn(false);
        setUsername('');
        window.location.reload();
    };

    const toggleSearchBar = () => {
        setShowSearchBar(!showSearchBar);
    };

    return (
        <nav className="nav-container">            <div className="nav-bar">
                <button className="nav-button" onClick={onMoviesClick}>Strona główna</button>
                <button className="nav-button">Seriale</button>
                <button className="nav-button">Rankingi</button>
                <button className="nav-button" onClick={onLibraryClick}>Moja biblioteka</button>
                <button className="nav-button search-icon" onClick={toggleSearchBar}>
                    <i className="fa fa-search">🔍</i>
                </button>
                {isLoggedIn ? (
                    <div className="user-menu">
                        <span className="username">Witaj, {username}</span>
                        <button className="nav-button logout-button" onClick={handleLogout}>Wyloguj</button>
                    </div>
                ) : (
                    <button className="nav-button login-button" onClick={() => setShowLoginModal(true)}>
                        Zaloguj się
                    </button>
                )}
            </div>
            
            {showSearchBar && <SearchBar onClose={() => setShowSearchBar(false)} />}
            {showLoginModal && <LoginModal onClose={() => setShowLoginModal(false)} onLoginSuccess={() => setIsLoggedIn(true)} />}
        </nav>
    );
};

export default NavBar;