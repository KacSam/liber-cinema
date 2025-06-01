import React, { useState, useEffect } from 'react';
import { Movie } from '../types/movie';
import { UserListType } from '../types/userListType';
import './UserLibrary.css';

const UserLibrary: React.FC = () => {
    const [userLists, setUserLists] = useState<{[key: string]: Movie[]}>({
        'WATCHLIST': [],
        'WATCHED': [],
        'FAVORITES': []
    });
    const [activeTab, setActiveTab] = useState<UserListType>('WATCHLIST');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');    useEffect(() => {
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
    }, []);const fetchUserLists = async () => {
        setLoading(true);
        setError('');
        
        try {
            const token = localStorage.getItem('token');
            
            console.log('Token użyty do autoryzacji:', token ? `${token.substring(0, 15)}...` : 'brak');
            
            if (!token) {
                setError('Musisz być zalogowany, aby zobaczyć swoją bibliotekę');
                setLoading(false);
                return;
            }
            
            // Pobieramy listy filmów użytkownika
            console.log('Wysyłanie żądania do /api/userlists/my-lists');
            const response = await fetch('http://localhost:8080/api/userlists/my-lists', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            console.log('Status odpowiedzi:', response.status);
            console.log('Nagłówki odpowiedzi:', Object.fromEntries([...response.headers.entries()]));
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Treść błędu:', errorText);
                throw new Error(`Błąd podczas pobierania list: ${response.status}`);
            }
            
            const data = await response.json();
            console.log('Pobrane dane z list użytkownika (surowe):', data);
            
            // Grupujemy filmy według typu listy
            const lists: {[key: string]: Movie[]} = {
                'WATCHLIST': [],
                'WATCHED': [],
                'FAVORITES': []
            };
            
            // Sprawdźmy, co dokładnie zawierają dane
            if (Array.isArray(data)) {
                console.log(`Otrzymano ${data.length} elementów listy`);
                data.forEach((item: any, index: number) => {
                    console.log(`Element ${index}:`, item);
                    console.log(`- Typ listy: ${item.userListType}`);
                    console.log(`- Film:`, item.movie);
                    
                    const listType = item.userListType;
                    const movie = item.movie;
                    
                    if (movie && lists[listType]) {
                        lists[listType].push(movie);
                        console.log(`Dodano film "${movie.title}" do listy ${listType}`);
                    } else {
                        console.log(`Pominięto element - listType: ${listType}, movie:`, movie);
                    }
                });
            } else {
                console.log('Otrzymane dane nie są tablicą!');
            }
            
            console.log('Pogrupowane listy filmów:', lists);
            setUserLists(lists);
        } catch (err) {
            console.error('Błąd podczas pobierania list użytkownika:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        } finally {
            setLoading(false);
        }
    };

    const renderMoviesList = (movies: Movie[]) => {
        if (movies.length === 0) {
            return <p className="empty-list">Brak filmów na tej liście</p>;
        }
        
        return (
            <div className="movies-grid">
                {movies.map((movie) => (
                    <div key={movie.id} className="movie-card">
                        <h3>{movie.title}</h3>
                        <p><strong>Reżyser:</strong> {movie.director}</p>
                        <p><strong>Gatunek:</strong> {movie.genre}</p>
                        <p><strong>Ocena IMDB:</strong> {movie.imdbRating}</p>
                    </div>
                ))}
            </div>
        );
    };
    
    // Mapowanie typów list na nazwy przyjazne dla użytkownika
    const listTypeNames = {
        'WATCHLIST': 'Do obejrzenia',
        'WATCHED': 'Obejrzane',
        'FAVORITES': 'Ulubione'
    };    return (
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
        </div>
    );
};

export default UserLibrary;
