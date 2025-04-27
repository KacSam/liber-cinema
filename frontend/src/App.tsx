import React, { useState } from 'react';
import NavBar from './components/NavBar';
import BooksList from './components/BooksList';
import './App.css';

const App: React.FC = () => {
    const [showBooks, setShowBooks] = useState<boolean>(false);

    const handleBooksClick = () => {
        setShowBooks(!showBooks); // Toggle books visibility
    };

    return (
        <div className="App">
            <NavBar onBooksClick={handleBooksClick} />
            {showBooks && <BooksList />}
        </div>
    );
};

export default App;