import React, { useState } from 'react';
import "../styles/Login.css"
function Login({ onLoginSuccess }) {
    const [voterEmail, setVoterEmail] = useState('');
    const [voterPassword, setVoterPassword] = useState('');

    // Handle the form submission
    const handleLogin = async (e) => {
        e.preventDefault();

        const loginData = {
            voterEmail: voterEmail.trim(),  // Make sure there's no leading/trailing space
            voterPassword: voterPassword.trim(),
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
                console.log("Login successful", data);
                onLoginSuccess();  // Notify App component about login success
            } else {
                console.error("Login failed:", data);  // Log failure details
                alert(data.message || 'Login failed');
            }
        } catch (error) {
            console.error('Login error:', error);  // Log the actual error
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
                    <label htmlFor="voterPassword">Password:</label>
                    <input
                        type="password"
                        id="voterPassword"
                        value={voterPassword}
                        onChange={(e) => setVoterPassword(e.target.value)}
                    />
                </div>
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

export default Login;
