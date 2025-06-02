import React, { useState, useEffect } from 'react';
import './FriendsPage.css';

interface Friend {
    id: number;
    friend: {
        username: string;
        email: string;
    };
    createdAt: string;
}

interface FriendRequest {
    id: number;
    username: string;
    email: string;
    requestedAt: string;
}

interface UserSearchResult {
    id: number;
    username: string;
    email: string;
}

const FriendsPage: React.FC = () => {
    const [friends, setFriends] = useState<Friend[]>([]);
    const [pendingRequests, setPendingRequests] = useState<FriendRequest[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [newFriendUsername, setNewFriendUsername] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState<UserSearchResult[]>([]);
    const [isSearching, setIsSearching] = useState(false);
    
    useEffect(() => {
        fetchFriends();
        fetchPendingRequests();
    }, []);
    
    const fetchFriends = async () => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby zobaczyć listę znajomych');
                setLoading(false);
                return;
            }
            
            const response = await fetch('http://localhost:8080/api/friends', {
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
                    throw new Error(`Błąd podczas pobierania listy znajomych: ${response.status}`);
                }
                return;
            }
            
            const data = await response.json();
            setFriends(data);
        } catch (err) {
            console.error('Błąd podczas pobierania listy znajomych:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas pobierania danych');
        } finally {
            setLoading(false);
        }
    };
    
    const fetchPendingRequests = async () => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch('http://localhost:8080/api/friends/pending', {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                if (response.status !== 401) {
                    throw new Error(`Błąd podczas pobierania zaproszeń: ${response.status}`);
                }
                return;
            }
            
            const data = await response.json();
            setPendingRequests(data);
        } catch (err) {
            console.error('Błąd podczas pobierania zaproszeń:', err);
        }
    };
    
    const handleAddFriend = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!newFriendUsername.trim()) {
            return;
        }
        
        setIsSubmitting(true);
        setError('');
        setSuccessMessage('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby dodać znajomego');
                setIsSubmitting(false);
                return;
            }
            
            const response = await fetch('http://localhost:8080/api/friends/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ username: newFriendUsername })
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                setError(data.message || 'Wystąpił błąd podczas dodawania znajomego');
                return;
            }
            
            setSuccessMessage(data.message || 'Zaproszenie do znajomych zostało wysłane');
            setNewFriendUsername('');
            
            // Odśwież listy
            fetchFriends();
            fetchPendingRequests();
        } catch (err) {
            console.error('Błąd podczas dodawania znajomego:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        } finally {
            setIsSubmitting(false);
        }
    };
    
    const handleAcceptRequest = async (id: number) => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch(`http://localhost:8080/api/friends/${id}/accept`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Nie udało się zaakceptować zaproszenia');
            }
            
            // Odśwież listy
            fetchFriends();
            fetchPendingRequests();
            setSuccessMessage('Zaproszenie do znajomych zostało zaakceptowane');
        } catch (err) {
            console.error('Błąd podczas akceptowania zaproszenia:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        }
    };
    
    const handleRejectRequest = async (id: number) => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch(`http://localhost:8080/api/friends/${id}/reject`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Nie udało się odrzucić zaproszenia');
            }
            
            // Odśwież listę zaproszeń
            fetchPendingRequests();
            setSuccessMessage('Zaproszenie do znajomych zostało odrzucone');
        } catch (err) {
            console.error('Błąd podczas odrzucania zaproszenia:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        }
    };
    
    const handleRemoveFriend = async (id: number) => {
        try {
            const token = localStorage.getItem('token');
            
            if (!token) return;
            
            const response = await fetch(`http://localhost:8080/api/friends/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (!response.ok) {
                throw new Error('Nie udało się usunąć znajomego');
            }
            
            // Odśwież listę znajomych
            fetchFriends();
            setSuccessMessage('Znajomy został usunięty');
        } catch (err) {
            console.error('Błąd podczas usuwania znajomego:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        }    };
    
    const handleSearch = async () => {
        if (!searchQuery.trim()) {
            return;
        }
        
        setIsSearching(true);
        setError('');
        setSuccessMessage('');
        setSearchResults([]);
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby wyszukiwać użytkowników');
                setIsSearching(false);
                return;
            }
            
            const response = await fetch(`http://localhost:8080/api/friends/search?query=${encodeURIComponent(searchQuery)}`, {
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
                    throw new Error(`Błąd podczas wyszukiwania użytkowników: ${response.status}`);
                }
                return;
            }
            
            const data = await response.json();
            setSearchResults(data);
            
            if (data.length === 0) {
                setSuccessMessage('Nie znaleziono użytkowników pasujących do wyszukiwania');
            }
        } catch (err) {
            console.error('Błąd podczas wyszukiwania użytkowników:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd podczas wyszukiwania');
        } finally {
            setIsSearching(false);
        }
    };
    
      const handleAddFriendRequest = async (userId: number) => {
        setIsSubmitting(true);
        setError('');
        setSuccessMessage('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setError('Musisz być zalogowany, aby dodać znajomego');
                setIsSubmitting(false);
                return;
            }
            
            const response = await fetch(`http://localhost:8080/api/friends/add-by-id`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ userId })
            });
            
            const data = await response.json();
            
            if (!response.ok) {
                setError(data.message || 'Wystąpił błąd podczas dodawania znajomego');
                return;
            }
            
            setSuccessMessage(data.message || 'Zaproszenie do znajomych zostało wysłane');
            
            // Remove the user from search results
            setSearchResults(prev => prev.filter(user => user.id !== userId));
            
            // Refresh friends and pending requests lists
            fetchFriends();
            fetchPendingRequests();
        } catch (err) {
            console.error('Błąd podczas dodawania znajomego:', err);
            setError(err instanceof Error ? err.message : 'Wystąpił błąd');
        } finally {
            setIsSubmitting(false);
        }
    };
      const formatDate = (dateString: string) => {
        if (!dateString) {
            console.log('Brak daty - dateString jest null lub undefined');
            return 'Brak daty';
        }
        
        try {
            console.log('Oryginalna data:', dateString);
            const date = new Date(dateString);
            
            // Sprawdzamy, czy data jest ważna
            if (isNaN(date.getTime())) {
                console.log('Nieprawidłowa data - nie można przekonwertować na obiekt Date');
                return 'Brak daty';
            }
            
            const formattedDate = date.toLocaleDateString('pl-PL', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
            console.log('Sformatowana data:', formattedDate);
            return formattedDate;
        } catch (error) {
            console.error('Błąd formatowania daty:', error);
            return 'Brak daty';
        }
    };
    
    if (loading) {
        return <div className="friends-page">Ładowanie...</div>;
    }
    
    if (error && friends.length === 0 && pendingRequests.length === 0) {
        return <div className="friends-page error">{error}</div>;
    }
    
    return (
        <div className="friends-page">
            <h1>Znajomi</h1>
            
            {error && <div className="error-message">{error}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}
              <div className="add-friend-form">
                <h2>Dodaj znajomego</h2>
                <form onSubmit={handleAddFriend}>
                    <input
                        type="text"
                        placeholder="Wpisz nazwę użytkownika"
                        value={newFriendUsername}
                        onChange={(e) => setNewFriendUsername(e.target.value)}
                        disabled={isSubmitting}
                    />
                    <button type="submit" disabled={isSubmitting || !newFriendUsername.trim()}>
                        {isSubmitting ? 'Dodawanie...' : 'Dodaj znajomego'}
                    </button>
                </form>
                
                <div className="search-users-form">
                    <h3>Lub wyszukaj użytkowników:</h3>
                    <div className="search-input-container">
                        <input
                            type="text"
                            placeholder="Szukaj użytkowników"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            disabled={isSearching}
                        />
                        <button 
                            onClick={handleSearch} 
                            disabled={isSearching || !searchQuery.trim()}
                        >
                            {isSearching ? 'Szukam...' : 'Szukaj'}
                        </button>
                    </div>
                    
                    {searchResults.length > 0 && (
                        <div className="search-results">
                            <h4>Wyniki wyszukiwania:</h4>
                            <div className="search-results-list">
                                {searchResults.map(user => (
                                    <div key={user.id} className="search-result-item">
                                        <div className="user-info">
                                            <span className="username">{user.username}</span>
                                            <span className="email">{user.email}</span>
                                        </div>
                                        <button 
                                            className="add-friend-button"
                                            onClick={() => handleAddFriendRequest(user.id)}
                                            disabled={isSubmitting}
                                        >
                                            Dodaj
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>            </div>
            
            {pendingRequests.length > 0 && (
                <div className="pending-requests">
                    <h2>Oczekujące zaproszenia ({pendingRequests.length})</h2>
                    <div className="requests-list">
                        {pendingRequests.map((request) => (
                            <div key={request.id} className="request-item">
                                <div className="request-info">
                                    <span className="request-username">{request.username}</span>
                                    <span className="request-date">Data zaproszenia: {formatDate(request.requestedAt)}</span>
                                </div>
                                <div className="request-actions">
                                    <button 
                                        className="accept-button"
                                        onClick={() => handleAcceptRequest(request.id)}
                                    >
                                        Akceptuj
                                    </button>
                                    <button 
                                        className="reject-button"
                                        onClick={() => handleRejectRequest(request.id)}
                                    >
                                        Odrzuć
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
            
            <div className="friends-list-container">
                <h2>Twoi znajomi ({friends.length})</h2>
                {friends.length === 0 ? (
                    <p className="empty-list">Nie masz jeszcze żadnych znajomych</p>
                ) : (                    <div className="friends-list">
                        {friends.map((friend) => (
                            <div key={friend.id} className="friend-item">                                <div className="friend-info">
                                    <span className="friend-username">{friend.friend.username}</span>
                                    <span className="friend-email">{friend.friend.email}</span>
                                </div>
                                <button 
                                    className="remove-button"
                                    onClick={() => handleRemoveFriend(friend.id)}
                                >
                                    Usuń
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default FriendsPage;
