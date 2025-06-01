import React, { useState, useEffect } from 'react';
import './AddToListModal.css';
import { Movie } from '../types/movie';

interface AddToListModalProps {
    movie: Movie;
    onClose: () => void;
}

const AddToListModal: React.FC<AddToListModalProps> = ({ movie, onClose }) => {
    const [listType, setListType] = useState('');
    const [rating, setRating] = useState<number | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    
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

        // Sprawdź, czy ocena została dodana dla obejrzanych
        if (listType === 'WATCHED' && !rating) {
            setError('Dodaj ocenę dla obejrzanego filmu');
            return;
        }
        
        setIsSubmitting(true);
        setError('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                throw new Error('Nie jesteś zalogowany lub token wygasł');
            }
            
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
                listType: listType,
                userRating: rating
            };

            const response = await fetch('http://localhost:8080/api/movies/add-to-list', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(requestData)
            });
            
            if (!response.ok) {
                if (response.status === 401) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    throw new Error('Sesja wygasła. Zaloguj się ponownie.');
                }
                
                let errorMessage = 'Nie udało się dodać filmu do listy';
                try {
                    const errorData = await response.json();
                    if (errorData.message) {
                        errorMessage = errorData.message;
                    }
                } catch (e) {
                    const errorText = await response.text();
                    console.error("Error response text:", errorText);
                }
                
                throw new Error(`${errorMessage} (HTTP ${response.status})`);
            }
            
            const refreshEvent = new CustomEvent('refreshUserLibrary');
            window.dispatchEvent(refreshEvent);
            
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

    const RatingStars = () => {
        return (
            <div className="rating-stars">
                {[...Array(10)].map((_, index) => {
                    const ratingValue = index + 1;
                    return (
                        <button
                            key={index}
                            type="button"
                            className={`star-button ${ratingValue <= (rating || 0) ? 'filled' : ''}`}
                            onClick={() => setRating(ratingValue)}
                        >
                            ★
                        </button>
                    );
                })}
                {rating !== null && rating > 0 && <span className="rating-value">{rating}/10</span>}
            </div>
        );
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
                        <div className="list-options">
                            <label className="list-option">
                                <input
                                    type="radio"
                                    name="listType"
                                    value="WATCHLIST"
                                    onChange={() => {
                                        setListType('WATCHLIST');
                                        setRating(null);
                                    }}
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
                        </div>
                    </div>

                    {listType === 'WATCHED' && (
                        <div className="form-group">
                            <label>Twoja ocena:</label>
                            <RatingStars />
                        </div>
                    )}
                    
                    <button 
                        type="submit" 
                        className="submit-button"
                        disabled={isSubmitting || !listType || (listType === 'WATCHED' && !rating)}
                    >
                        {isSubmitting ? 'Dodawanie...' : 'Dodaj do listy'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default AddToListModal;
