import React, { useState } from 'react';
import './LoginModal.css';

interface LoginModalProps {
    onClose: () => void;
    onLoginSuccess: () => void;
}

const LoginModal: React.FC<LoginModalProps> = ({ onClose, onLoginSuccess }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');        try {
            if (isLogin) {
                // Logowanie
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ username, password }),
                });
                
                if (!response.ok) {
                    throw new Error('Nieprawidłowy login lub hasło');
                }                const data = await response.json();
                const token = data.token;
                
                if (!token) {
                    throw new Error("Brak tokena w odpowiedzi z serwera");
                }                // Zapisz tokeny JWT w localStorage
                localStorage.setItem('token', token);
                localStorage.setItem('refreshToken', data.refreshToken);
                localStorage.setItem('user', JSON.stringify({
                    id: data.id,
                    username: data.username,
                    email: data.email
                }));
                
                setSuccess('Zalogowano pomyślnie!');
                setTimeout(() => {
                    onClose();
                    onLoginSuccess();
                }, 1000);
            } else {
                // Rejestracja
                const response = await fetch('http://localhost:8080/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ username, email, password }),
                });

                const data = await response.json();

                if (!response.ok) {
                    throw new Error(data.message || 'Błąd rejestracji');
                }

                setSuccess('Konto zostało utworzone! Możesz się teraz zalogować.');
                setTimeout(() => {
                    setIsLogin(true);
                    setUsername('');
                    setPassword('');
                    setEmail('');
                    setSuccess('');
                }, 2000);
            }        } catch (err) {
            setError(err instanceof Error ? err.message : 'Wystąpił nieznany błąd');
        }
    };

    return (
        <div className="modal-backdrop">
            <div className="modal-content">
                <button className="close-button" onClick={onClose}>×</button>
                
                <h2>{isLogin ? 'Logowanie' : 'Rejestracja'}</h2>
                
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="username">Nazwa użytkownika</label>
                        <input
                            type="text"
                            id="username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    
                    {!isLogin && (
                        <div className="form-group">
                            <label htmlFor="email">Email</label>
                            <input
                                type="email"
                                id="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>
                    )}
                    
                    <div className="form-group">
                        <label htmlFor="password">Hasło</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    
                    <button type="submit" className="submit-button">
                        {isLogin ? 'Zaloguj się' : 'Zarejestruj się'}
                    </button>
                </form>
                
                <div className="toggle-form">
                    {isLogin ? (
                        <p>Nie masz konta? <button onClick={() => setIsLogin(false)}>Zarejestruj się</button></p>
                    ) : (
                        <p>Masz już konto? <button onClick={() => setIsLogin(true)}>Zaloguj się</button></p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default LoginModal;
