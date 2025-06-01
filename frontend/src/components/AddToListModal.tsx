import React, { useState, useEffect } from 'react';
import './AddToListModal.css';
import { Movie } from '../types/movie';

interface AddToListModalProps {
    movie: Movie;
    onClose: () => void;
}

const AddToListModal: React.FC<AddToListModalProps> = ({ movie, onClose }) => {
    const [listType, setListType] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    
    // Funkcja do debugowania tokena
    useEffect(() => {
        const token = localStorage.getItem('token');
        console.log("Current token:", token);
    }, []);
    
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!listType) {
            setError('Wybierz typ listy');
            return;
        }
        
        setIsSubmitting(true);
        setError('');
        
        try {
            // Pobierz token z localStorage
            const token = localStorage.getItem('token');
            
            // Debug informacja
            console.log("Using token from storage:", token ? `${token.substring(0, 15)}...` : 'null');
            
            if (!token) {
                throw new Error('Nie jesteś zalogowany lub token wygasł');
            }
            
            // Przygotuj obiekt żądania
            const requestData = {
                movie: {
                    id: movie.id,
                    title: movie.title,
                    director: movie.director,
                    description: movie.description,
                    genre: movie.genre,
                    releaseDate: movie.releaseDate,
                    duration: movie.duration,
                    imdbRating: movie.imdbRating
                },
                listType: listType
            };
              console.log("Sending request data:", requestData);
              // Konfiguracja fetch z tokenem
            const response = await fetch('http://localhost:8080/api/movies/add-to-list', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(requestData)
            });
            
            console.log("Response status:", response.status);
            
            if (!response.ok) {
                // Obsługa błędów
                if (response.status === 401) {
                    // Problem z autoryzacją
                    console.error("Authorization failed. Token might be invalid or expired.");
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    throw new Error('Sesja wygasła. Zaloguj się ponownie.');
                }
                
                let errorMessage = 'Nie udało się dodać filmu do listy';
                try {
                    const errorData = await response.json();
                    console.error("Error response:", errorData);
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    const errorText = await response.text();
                    console.error("Error response text:", errorText);
                }
                
                throw new Error(`${errorMessage} (HTTP ${response.status})`);
            }
            
            // Publish a custom event to notify UserLibrary to refresh
            const refreshEvent = new CustomEvent('refreshUserLibrary');
            window.dispatchEvent(refreshEvent);
            console.log("Published refreshUserLibrary event");
            
            setSuccess('Film został dodany do listy!');
            setTimeout(() => {
                onClose();
            }, 2000);
            
        } catch (err) {
            console.error('Error adding movie to list:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        } finally {
            setIsSubmitting(false);
        }
    };
    
    return (
        <div className="modal-backdrop">
            <div className="modal-content add-list-modal">
                <button className="close-button" onClick={onClose}>×</button>
                
                <h2>Dodaj film do listy</h2>
                <h3>{movie.title}</h3>
                
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Wybierz listę:</label>
                        <div className="list-options">                            <label className="list-option">
                                <input
                                    type="radio"
                                    name="listType"
                                    value="WATCHLIST"
                                    onChange={() => setListType('WATCHLIST')}
                                    checked={listType === 'WATCHLIST'}
                                />
                                <span>Do obejrzenia</span>
                            </label>
                            
                            <label className="list-option">
                                <input
                                    type="radio"
                                    name="listType"
                                    value="WATCHED"
                                    onChange={() => setListType('WATCHED')}
                                    checked={listType === 'WATCHED'}
                                />
                                <span>Obejrzane</span>
                            </label>
                            
                            <label className="list-option">
                                <input
                                    type="radio"
                                    name="listType"
                                    value="FAVORITES"
                                    onChange={() => setListType('FAVORITES')}
                                    checked={listType === 'FAVORITES'}
                                />
                                <span>Ulubione</span>
                            </label>
                        </div>
                    </div>
                    
                    <button 
                        type="submit" 
                        className="submit-button"
                        disabled={isSubmitting || !listType}
                    >
                        {isSubmitting ? 'Dodawanie...' : 'Dodaj do listy'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddToListModal;
