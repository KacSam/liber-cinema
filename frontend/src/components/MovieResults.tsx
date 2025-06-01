import React, { useState } from 'react';
import './MovieResults.css';
import { Movie } from '../types/movie';
import AddToListModal from './AddToListModal';
import LoginModal from './LoginModal';

interface MovieResultsProps {
    movies: Movie[];
}

const MovieResults: React.FC<MovieResultsProps> = ({ movies }) => {
    const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null);
    const [showAddToListModal, setShowAddToListModal] = useState(false);
    const [showLoginModal, setShowLoginModal] = useState(false);
    
    const handleAddToList = (movie: Movie) => {
        const token = localStorage.getItem('token');
        
        if (!token) {
            // Jeśli użytkownik nie jest zalogowany, pokaż modal logowania
            setSelectedMovie(movie);
            setShowLoginModal(true);
            return;
        }
        
        setSelectedMovie(movie);
        setShowAddToListModal(true);
    };
    
    const handleLoginSuccess = () => {
        setShowLoginModal(false);
        // Jeśli był wybrany film przed logowaniem, pokaż modal dodawania do listy
        if (selectedMovie) {
            setShowAddToListModal(true);
        }
    };
    
    return (
        <div className="movie-results">
            <h2>Wyniki wyszukiwania</h2>
            
            <div className="movies-grid">
                {movies.map((movie, index) => (
                    <div key={index} className="movie-card">
                        <h3>{movie.title}</h3>
                        {movie.releaseDate && <p><strong>Data premiery:</strong> {movie.releaseDate}</p>}
                        {movie.director && <p><strong>Reżyser:</strong> {movie.director}</p>}
                        {movie.genre && <p><strong>Gatunek:</strong> {movie.genre}</p>}
                        {movie.duration && <p><strong>Czas trwania:</strong> {movie.duration}</p>}
                        {movie.imdbRating && <p><strong>Ocena IMDB:</strong> {movie.imdbRating}/10</p>}
                        
                        {movie.description && (
                            <div className="movie-description">
                                <p>{movie.description}</p>
                            </div>
                        )}
                        
                        <button 
                            className="add-to-list-button" 
                            onClick={() => handleAddToList(movie)}
                        >
                            Dodaj do listy
                        </button>
                    </div>
                ))}
            </div>
            
            {showAddToListModal && selectedMovie && (
                <AddToListModal 
                    movie={selectedMovie} 
                    onClose={() => setShowAddToListModal(false)} 
                />
            )}
            
            {showLoginModal && (
                <LoginModal 
                    onClose={() => setShowLoginModal(false)} 
                    onLoginSuccess={handleLoginSuccess} 
                />
            )}
        </div>
    );
};

export default MovieResults;
