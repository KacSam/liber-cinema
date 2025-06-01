import React, { useState } from 'react';
import './SearchBar.css';
import MovieResults from './MovieResults';

interface SearchBarProps {
    onClose: () => void;
}

const SearchBar: React.FC<SearchBarProps> = ({ onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchType, setSearchType] = useState<'title' | 'genre'>('title');
    const [isSearching, setIsSearching] = useState(false);
    const [searchResults, setSearchResults] = useState<any[]>([]);
    const [error, setError] = useState('');

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!searchTerm.trim()) return;
          setIsSearching(true);
        setError('');
        
        try {
            const endpoint = searchType === 'title' 
                ? `http://localhost:8080/api/movies/search?title=${encodeURIComponent(searchTerm)}`
                : `http://localhost:8080/api/movies/search/genre?genre=${encodeURIComponent(searchTerm)}`;            
            console.log("Searching with endpoint:", endpoint);
            
            // Search is a public endpoint, no need for authorization
            const response = await fetch(endpoint);
            
            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error('Wystąpił błąd autoryzacji');
                }
                throw new Error('Nie znaleziono filmów');
            }
            
            const data = await response.json();
            
            // Dla wyszukiwania po tytule otrzymujemy pojedynczy film, dla gatunku - tablicę
            if (searchType === 'title') {
                setSearchResults([data]);
            } else {
                setSearchResults(data);
            }
        } catch (err) {
            console.error('Error searching movies:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas wyszukiwania');
            setSearchResults([]);
        } finally {
            setIsSearching(false);
        }
    };

    return (
        <div className="search-container">
            <div className="search-bar">
                <button className="close-search" onClick={onClose}>×</button>
                <form onSubmit={handleSearch}>
                    <div className="search-inputs">
                        <div className="search-type">
                            <button 
                                type="button"
                                className={`search-type-button ${searchType === 'title' ? 'active' : ''}`}
                                onClick={() => setSearchType('title')}
                            >
                                Tytuł
                            </button>
                            <button 
                                type="button"
                                className={`search-type-button ${searchType === 'genre' ? 'active' : ''}`}
                                onClick={() => setSearchType('genre')}
                            >
                                Gatunek
                            </button>
                        </div>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            placeholder={searchType === 'title' ? 'Wpisz tytuł filmu...' : 'Wpisz gatunek filmu...'}
                            className="search-input"
                        />
                        <button type="submit" className="search-button">Szukaj</button>
                    </div>
                </form>
            </div>
            
            {isSearching && <div className="loading">Szukam filmów...</div>}
            
            {error && <div className="search-error">{error}</div>}
            
            {searchResults.length > 0 && !isSearching && (
                <MovieResults movies={searchResults} />
            )}
        </div>
    );
};

export default SearchBar;
