import React, { useState, useEffect } from 'react';
import { Book } from '../types/book';
import './BooksList.css';

const BooksList: React.FC = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                const response = await fetch('http://localhost:8080/books');
                if (!response.ok) {
                    throw new Error('Problem z pobraniem danych');
                }
                const data: Book[] = await response.json();
                setBooks(data);
            } catch (err) {
                setError(err instanceof Error ? err.message : 'Wystąpił nieznany błąd');
            } finally {
                setLoading(false);
            }
        };

        fetchBooks();
    }, []);

    if (loading) return <div className="loading">Ładowanie...</div>;
    if (error) return <div className="error">Błąd: {error}</div>;

    return (
        <div className="books-container">
            <h2>Lista książek</h2>
            <div className="books-grid">
                {books.map(book => (
                    <div key={book.id} className="book-card">
                        <h3>{book.title}</h3>
                        <p><strong>Autor:</strong> {book.author}</p>
                        <p><strong>Gatunek:</strong> {book.genre}</p>
                        <p><strong>Data publikacji:</strong> {new Date(book.publicationDate).toLocaleDateString()}</p>
                        <p><strong>Ocena:</strong> {book.externalRating || 'Brak oceny'}</p>
                        <div className="book-description">
                            <p>{book.description || 'Brak opisu'}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default BooksList;