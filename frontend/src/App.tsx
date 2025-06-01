import React, { useState } from 'react';
import './App.css';
import NavBar from './components/NavBar';
import MoviesList from './components/MoviesList';
import UserLibrary from './components/UserLibrary';

function App() {
  const [showMovies, setShowMovies] = useState(false);
  const [showLibrary, setShowLibrary] = useState(false);

  const handleMoviesClick = () => {
    setShowMovies(true);
    setShowLibrary(false);
  };

  const handleLibraryClick = () => {
    setShowLibrary(true);
    setShowMovies(false);
  };
  return (
    <div className="App">
      <NavBar onMoviesClick={handleMoviesClick} onLibraryClick={handleLibraryClick} />
      
      <div className="content">
        {showMovies && <MoviesList />}
        {showLibrary && <UserLibrary />}
      </div>
    </div>
  );
}

export default App;