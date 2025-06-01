import React, { useState, useEffect } from 'react';
import { Movie } from '../types/movie';
import { UserListType } from '../types/userListType';
import './UserLibrary.css';

interface RatingStarsProps {
    movieId: number;
    initialRating?: number;
    onRatingChange: (rating: number) => void;
}

const RatingStars: React.FC<RatingStarsProps> = ({ movieId, initialRating, onRatingChange }) => {
    const [rating, setRating] = useState(initialRating || 0);
    const [hover, setHover] = useState(0);

    const handleRating = async (value: number) => {
        const token = localStorage.getItem('token');
        if (!token) return;

        try {
            const response = await fetch(`http://localhost:8080/api/ratings/movies/${movieId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ rating: value })
            });

            if (response.ok) {
                setRating(value);
                onRatingChange(value);
            }
        } catch (error) {
            console.error('Error setting rating:', error);
        }
    };

    return (
        <div className="rating-stars">
            {[...Array(10)].map((_, index) => {
                const ratingValue = index + 1;
                return (
                    <button
                        key={index}
                        type="button"
                        className={`star-button ${ratingValue <= (hover || rating) ? 'filled' : ''}`}
                        onClick={() => handleRating(ratingValue)}
                        onMouseEnter={() => setHover(ratingValue)}
                        onMouseLeave={() => setHover(0)}
                    >
                        ★
                    </button>
                );
            })}
            {rating > 0 && <span className="rating-value">{rating}/10</span>}
        </div>
    );
};

interface MoveToWatchedModalProps {
    movie: Movie;
    onClose: () => void;
    onSave: (rating: number) => void;
}

const MoveToWatchedModal: React.FC<MoveToWatchedModalProps> = ({ movie, onClose, onSave }) => {
    const [rating, setRating] = useState<number | null>(null);
    const [error, setError] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!rating) {
            setError('Musisz dodać ocenę filmu');
            return;
        }
        onSave(rating);
    };

    return (
        <div className="modal-backdrop">
            <div className="modal-content">
                <button className="close-button" onClick={onClose}>×</button>
                <h2>Przenieś do obejrzanych</h2>
                <h3>{movie.title}</h3>
                {error && <div className="error-message">{error}</div>}
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Twoja ocena:</label>
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
                            {rating !== null && <span className="rating-value">{rating}/10</span>}
                        </div>
                    </div>
                    <button 
                        type="submit" 
                        className="submit-button"
                        disabled={!rating}
                    >
                        Przenieś do obejrzanych
                    </button>
                </form>
            </div>
        </div>
    );
};

const UserLibrary: React.FC = () => {
    const [userLists, setUserLists] = useState<{[key: string]: Movie[]}>({
        'WATCHLIST': [],
        'WATCHED': []
    });
    const [activeTab, setActiveTab] = useState<UserListType>('WATCHLIST');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [moveToWatchedMovie, setMoveToWatchedMovie] = useState<Movie | null>(null);

    useEffect(() => {
        fetchUserLists();
        
        // Add event listener for refreshUserLibrary event
        const handleRefresh = () => {
            console.log("Received refreshUserLibrary event");
            fetchUserLists();
        };
        
        window.addEventListener('refreshUserLibrary', handleRefresh);
        
        // Cleanup function
        return () => {
            window.removeEventListener('refreshUserLibrary', handleRefresh);
        };
    }, []);

    const fetchUserLists = async () => {
        setLoading(true);
        setError('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby zobaczyć swoją bibliotekę');
                setLoading(false);
                return;
            }

            const response = await fetch('http://localhost:8080/api/movies/my-lists', {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                if (response.status === 401) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    setError('Twoja sesja wygasła. Zaloguj się ponownie.');
                    window.dispatchEvent(new CustomEvent('showLoginModal'));
                } else {
                    const errorText = await response.text();
                    console.error('Treść błędu:', errorText);
                    throw new Error(`Błąd podczas pobierania list: ${response.status}`);
                }
                return;
            }
            
            const data = await response.json();
            console.log('Raw response data from my-lists:', data);
            
            const lists: {[key: string]: Movie[]} = {
                'WATCHLIST': [],
                'WATCHED': []
            };
            
            // Check if data is in the expected format
            if (data.WATCHLIST && data.WATCHED) {
                // Process WATCHLIST
                data.WATCHLIST.forEach((item: any) => {
                    console.log('Processing WATCHLIST item:', item);
                    lists.WATCHLIST.push({
                        ...item.movie,
                        isFavorite: Boolean(item.favorite), // Note: check if it's 'favorite' instead of 'isFavorite'
                        userRating: item.userRating
                    });
                });
                
                // Process WATCHED
                data.WATCHED.forEach((item: any) => {
                    console.log('Processing WATCHED item:', item);
                    lists.WATCHED.push({
                        ...item.movie,
                        isFavorite: Boolean(item.favorite), // Note: check if it's 'favorite' instead of 'isFavorite'
                        userRating: item.userRating
                    });
                });

                // Log the final state for debugging
                console.log('Final processed lists:', {
                    WATCHLIST: lists.WATCHLIST.map(m => ({
                        id: m.id,
                        title: m.title,
                        isFavorite: m.isFavorite
                    })),
                    WATCHED: lists.WATCHED.map(m => ({
                        id: m.id,
                        title: m.title,
                        isFavorite: m.isFavorite
                    }))
                });
            }
            
            setUserLists(lists);
            return lists; // Return for debugging purposes
        } catch (err) {
            console.error('Błąd podczas pobierania list użytkownika:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas pobierania danych');
        } finally {
            setLoading(false);
        }
    };

    const handleMoveToWatched = async (movie: Movie, rating: number) => {
        const token = localStorage.getItem('token');
        if (!token) return;

        try {
            const response = await fetch('http://localhost:8080/api/movies/add-to-list', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
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
                    listType: 'WATCHED',
                    userRating: rating
                })
            });

            if (response.ok) {
                setMoveToWatchedMovie(null);
                fetchUserLists(); // Odśwież listy
            } else {
                throw new Error('Nie udało się przenieść filmu do obejrzanych');
            }
        } catch (error) {
            console.error('Error moving movie to watched:', error);
        }
    };    const handleRatingChange = async (movieId: number, rating: number) => {
        const token = localStorage.getItem('token');
        if (!token) return;

        try {
            const response = await fetch(`http://localhost:8080/api/userlists/movies/${movieId}/rating`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ rating })
            });

            if (response.ok) {
                // Tylko po pomyślnej aktualizacji na serwerze aktualizujemy stan lokalny
                const updatedMovies = (movies: Movie[]) => movies.map(movie => 
                    movie.id === movieId ? { ...movie, userRating: rating } : movie
                );

                setUserLists(prevLists => ({
                    ...prevLists,
                    'WATCHED': updatedMovies(prevLists['WATCHED'])
                }));
            } else {
                console.error('Failed to update rating');
                // Możemy tu dodać wyświetlanie błędu użytkownikowi
                throw new Error('Nie udało się zaktualizować oceny');
            }
        } catch (error) {
            console.error('Error updating rating:', error);
            // Tu możemy dodać wyświetlanie błędu użytkownikowi
        }
    };

    const handleToggleFavorite = async (movie: Movie) => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('Musisz być zalogowany, aby dodać film do ulubionych');
            return;
        }

        const newFavoriteStatus = !movie.isFavorite;
        console.log(`Toggling favorite for movie ${movie.title} (id: ${movie.id}) to ${newFavoriteStatus}`);
        
        // Optymistyczna aktualizacja UI
        setUserLists(prevLists => {
            const newLists = { ...prevLists };
            ['WATCHLIST', 'WATCHED'].forEach(listType => {
                newLists[listType] = prevLists[listType].map(m => 
                    m.id === movie.id ? { ...m, isFavorite: newFavoriteStatus } : m
                );
            });
            return newLists;
        });

        try {
            const response = await fetch(`http://localhost:8080/api/userlists/movies/${movie.id}/favorite`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ isFavorite: newFavoriteStatus })
            });

            const responseData = await response.text();
            console.log('Server response:', responseData);

            if (!response.ok) {
                // Cofnij zmiany w UI jeśli wystąpił błąd
                setUserLists(prevLists => {
                    const newLists = { ...prevLists };
                    ['WATCHLIST', 'WATCHED'].forEach(listType => {
                        newLists[listType] = prevLists[listType].map(m => 
                            m.id === movie.id ? { ...m, isFavorite: !newFavoriteStatus } : m
                        );
                    });
                    return newLists;
                });
                throw new Error('Nie udało się zaktualizować statusu ulubionego');
            }

            // Ważne: po udanej aktualizacji pobierz świeże dane
            const freshData = await fetchUserLists();
            console.log('Fresh data after toggle:', freshData);
        } catch (error) {
            console.error('Error toggling favorite:', error);
            setError('Nie udało się zaktualizować statusu ulubionego filmu');
        }
    };

    const renderMoviesList = (movies: Movie[]) => {
        if (movies.length === 0) {
            return <p className="empty-list">Brak filmów na tej liście</p>;
        }

        const displayedMovies = activeTab === 'WATCHED' && movies.some(m => m.isFavorite) 
            ? movies.sort((a, b) => (b.isFavorite ? 1 : 0) - (a.isFavorite ? 1 : 0))
            : movies;
        
        return (
            <div className="movies-grid">
                {displayedMovies.map((movie) => (
                    <div key={movie.id} className="movie-card">
                        <div className="movie-header">
                            <h3>{movie.title}</h3>
                            {activeTab === 'WATCHED' && movie.id && (
                                <button 
                                    className={`favorite-button ${movie.isFavorite ? 'active' : ''}`}
                                    onClick={() => handleToggleFavorite(movie)}
                                    title={movie.isFavorite ? "Usuń z ulubionych" : "Dodaj do ulubionych"}
                                >
                                    {movie.isFavorite ? '♥' : '♡'}
                                </button>
                            )}
                        </div>
                        <p><strong>Reżyser:</strong> {movie.director}</p>
                        <p><strong>Gatunek:</strong> {movie.genre}</p>
                        <p><strong>Ocena IMDB:</strong> {movie.imdbRating}</p>
                        {activeTab === 'WATCHED' && movie.id ? (
                            <div className="movie-rating">
                                <RatingStars 
                                    movieId={movie.id}
                                    initialRating={movie.userRating}
                                    onRatingChange={(rating) => handleRatingChange(movie.id!, rating)}
                                />
                            </div>
                        ) : (
                            movie.id && (
                                <button 
                                    className="move-to-watched-button"
                                    onClick={() => setMoveToWatchedMovie(movie)}
                                >
                                    Oznacz jako obejrzane
                                </button>
                            )
                        )}
                    </div>
                ))}
            </div>
        );
    };

    const listTypeNames = {
        'WATCHLIST': 'Do obejrzenia',
        'WATCHED': 'Obejrzane'
    };

    return (
        <div className="user-library">
            <h1>Moja biblioteka filmów</h1>
            
            <div className="library-actions">
                <button 
                    className="refresh-button" 
                    onClick={fetchUserLists} 
                    disabled={loading}
                >
                    {loading ? 'Odświeżanie...' : 'Odśwież listę'}
                </button>
            </div>
            
            {loading ? (
                <p>Ładowanie biblioteki...</p>
            ) : error ? (
                <div className="error-message">{error}</div>
            ) : (
                <>
                    <div className="library-tabs">
                        {Object.keys(userLists).map((listType) => (
                            <button 
                                key={listType}
                                className={`tab-button ${activeTab === listType ? 'active' : ''}`}
                                onClick={() => setActiveTab(listType as UserListType)}
                            >
                                {listTypeNames[listType as keyof typeof listTypeNames]} ({userLists[listType].length})
                            </button>
                        ))}
                    </div>
                    
                    <div className="library-content">
                        {renderMoviesList(userLists[activeTab])}
                    </div>
                </>
            )}

            {moveToWatchedMovie && (
                <MoveToWatchedModal
                    movie={moveToWatchedMovie}
                    onClose={() => setMoveToWatchedMovie(null)}
                    onSave={(rating) => handleMoveToWatched(moveToWatchedMovie, rating)}
                />
            )}
        </div>
    );
};

export default UserLibrary;
