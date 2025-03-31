import React, { useState } from 'react';
import "../styles/Login.css"
function Login({ onLoginSuccess }) {
    const [voterEmail, setVoterEmail] = useState('');
    const [voterHashedPassword, setVoterHashedPassword] = useState('');

    // Handle the form submission
    const handleLogin = async (e) => {
        e.preventDefault();

        const loginData = {
            voterEmail: voterEmail,
            voterHashedPassword: voterHashedPassword,
        };

        try {
            const response = await fetch('http://localhost:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            const data = await response.json();

            if (response.ok) {
                // Notify App component about login success
                onLoginSuccess();
            } else {
                alert(data.message || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);
        }
    };

    return (
        <div className="login-container">
            <form onSubmit={handleLogin}>
                <h1>Login</h1>
                <div>
                    <label htmlFor="voterEmail">Email:</label>
                    <input
                        type="text"
                        id="voterEmail"
                        value={voterEmail}
                        onChange={(e) => setVoterEmail(e.target.value)}
                    />
                </div>
                <div>
                    <label htmlFor="voterHashedPassword">Password:</label>
                    <input
                        type="password"
                        id="voterHashedPassword"
                        value={voterHashedPassword}
                        onChange={(e) => setVoterHashedPassword(e.target.value)}
                    />
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

export default Login;
