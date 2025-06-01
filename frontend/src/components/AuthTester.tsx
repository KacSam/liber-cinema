import React, { useState } from 'react';

const AuthTester: React.FC = () => {
    const [testResult, setTestResult] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    
    const testAuth = async () => {
        setIsLoading(true);
        setTestResult('');
        
        try {
            const token = localStorage.getItem('token');
            
            if (!token) {
                setTestResult('Brak tokena w localStorage');
                return;
            }
            
            // Test endpointu wymagającego autentykacji
            const response = await fetch('http://localhost:8080/api/user/me', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            
            if (response.ok) {
                const data = await response.json();
                setTestResult(`Test autentykacji udany! Użytkownik: ${data.username}`);
            } else {
                setTestResult(`Test autentykacji nieudany. Status: ${response.status}`);
            }
        } catch (err) {
            setTestResult(`Błąd podczas testu: ${err instanceof Error ? err.message : 'Nieznany błąd'}`);
        } finally {
            setIsLoading(false);
        }
    };
    
    const resetToken = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setTestResult('Token został usunięty. Zaloguj się ponownie.');
    };
    
    return (
        <div style={{ margin: '20px', padding: '20px', border: '1px solid #ccc' }}>
            <h3>Tester autentykacji</h3>
            <div>
                <button onClick={testAuth} disabled={isLoading}>
                    {isLoading ? 'Testowanie...' : 'Testuj autentykację'}
                </button>
                <button onClick={resetToken} style={{ marginLeft: '10px' }}>
                    Zresetuj token
                </button>
            </div>
            {testResult && (
                <div style={{ marginTop: '10px', padding: '10px', background: '#f5f5f5' }}>
                    {testResult}
                </div>
            )}
        </div>
    );
};

export default AuthTester;
