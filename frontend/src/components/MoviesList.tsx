import React, { useState, useEffect } from 'react';
import { Movie } from '../types/movie';
import './MoviesList.css';

const MoviesList: React.FC = () => {
    const [movies, setMovies] = useState<Movie[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchMovies = async () => {
            try {
                const response = await fetch('http://localhost:8080/movies');
                if (!response.ok) {
                    throw new Error('Problem z pobraniem danych');
                }
                const data: Movie[] = await response.json();
                setMovies(data);
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Wystąpił nieznany błąd');
            } finally {
                setLoading(false);
            }
        };

        fetchMovies();
    }, []);

    if (loading) return <div className="loading">Ładowanie...</div>;
    if (error) return <div className="error">Błąd: {error}</div>;

    return (
        <div className="movies-container">
            <h2>Lista filmów</h2>
            <div className="movies-grid">
                {movies.map(movie => (
                    <div key={movie.id} className="movie-card">
                        <h3>{movie.title}</h3>
                        {movie.director && <p><strong>Reżyser:</strong> {movie.director}</p>}
                        {movie.genre && <p><strong>Gatunek:</strong> {movie.genre}</p>}
                        {movie.releaseDate && <p><strong>Data premiery:</strong> {movie.releaseDate}</p>}
                        {movie.duration && <p><strong>Czas trwania:</strong> {movie.duration}</p>}
                        {movie.imdbRating && <p><strong>Ocena IMDB:</strong> {movie.imdbRating}/10</p>}
                        <div className="movie-description">
                            <p>{movie.description || 'Brak opisu'}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default MoviesList;
